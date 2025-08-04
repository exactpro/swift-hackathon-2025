package com.exactpro.blockchain.coincento;

import com.exactpro.blockchain.entity.BankETHAddress;
import com.exactpro.blockchain.entity.Currency;
import com.exactpro.blockchain.repository.BankETHAddressRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.datatypes.*;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.utils.Numeric;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

@Component
public class CoincentoWallet {
    private static final Logger logger = LogManager.getLogger(CoincentoWallet.class);

    private static final BigDecimal XXX = BigDecimal.valueOf(10).pow(18);

    private final @NonNull Web3j web3j;

    private final @NonNull Credentials credentials;

    private final BankETHAddressRepository bankETHAddressRepository;

    @Value("${ethereum.coincento}")
    private String coincentoAddress;

    public CoincentoWallet(@NonNull Web3j web3j,
                           @NonNull Credentials credentials,
                           BankETHAddressRepository bankETHAddressRepository) {
        this.web3j = Objects.requireNonNull(web3j);
        this.credentials = Objects.requireNonNull(credentials);
        this.bankETHAddressRepository = bankETHAddressRepository;
    }

    public Mono<Void> transfer(
        @NonNull String creditorBic, @NonNull String endToEndId, @NonNull Currency currency, @NonNull BigDecimal amount
    ) {
        return
            Mono.zip(getEthereumAddress(creditorBic), getNonce())
            .flatMap(data -> {
                String toAddress = data.getT1();
                BigInteger nonce = data.getT2();
                Function safeTransfer = new Function(
                    "safeTransferFrom",
                    Arrays.asList(
                        new Address(credentials.getAddress()),
                        new Address(toAddress),
                        new Address(currency.getAddress()).toUint(),
                        new Uint(amount.multiply(XXX).toBigInteger()),
                        new DynamicBytes(endToEndId.getBytes(StandardCharsets.UTF_8))
                    ),
                    Collections.emptyList()
                );
                String payload = FunctionEncoder.encode(safeTransfer);
                BigInteger gasPrice = BigInteger.valueOf(20_000_000_000L);  // 20 Gwei
                BigInteger gasLimit = BigInteger.valueOf(200_000);          // adjust as needed

                RawTransaction rawTx = RawTransaction.createTransaction(
                    nonce,
                    gasPrice,
                    gasLimit,
                    coincentoAddress,
                    BigInteger.ZERO,
                    payload
                );

                long chainId = 1337L; // TODO: Get it from Ethereum node
                byte[] signedMessage = TransactionEncoder.signMessage(rawTx, chainId, credentials);
                String hexValue = Numeric.toHexString(signedMessage);

                return Mono.fromCompletionStage(() -> web3j.ethSendRawTransaction(hexValue).sendAsync())
                    .flatMap(it -> {
                        if (it.hasError()) {
                            return Mono.error(new Exception(it.getError().getMessage()));
                        }
                        return Mono.just(it);
                    })
                    .doOnSuccess(it -> logger.info("Sent transfer transaction {}", it.getTransactionHash()))
                    .then();
            });
    }

    @NonNull Mono<BigInteger> getNonce() {
        return
            Mono.fromCompletionStage(() ->
                web3j.ethGetTransactionCount(credentials.getAddress(), DefaultBlockParameterName.LATEST).sendAsync()
            )
            .map(EthGetTransactionCount::getTransactionCount);
    }

    private @NonNull Mono<String> getEthereumAddress(@NonNull String bic) {
        return bankETHAddressRepository.getByBic(bic).map(BankETHAddress::getEthAddress);
    }
}
