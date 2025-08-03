package com.exactpro.blockchain.coincento;

import com.exactpro.blockchain.entity.Currency;
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
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Numeric;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

@Component
public class CoincentoWallet {
    private static final Logger logger = LogManager.getLogger(CoincentoWallet.class);

    private static final BigDecimal XXX = BigDecimal.valueOf(10).pow(18);

    private final @NonNull Web3j web3j;

    private final @NonNull Credentials credentials;

    @Value("${ethereum.coincento}")
    private String coincentoAddress;

    public CoincentoWallet(@NonNull Web3j web3j, @NonNull Credentials credentials) {
        this.web3j = Objects.requireNonNull(web3j);
        this.credentials = Objects.requireNonNull(credentials);
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

                long chainId = 1337L;
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

    private static final Map<String, String> BIC_TO_ADDRESS = Map.of(
        "TESTUKLLXXX", "0xf17f52151EbEF6C7334FAD080c5704D77216b732",
        "TESTGETBXXX", "0xda6c0ca76e69b32c71301356043fb56d702dfb3d"
    );

    private @NonNull Mono<String> getEthereumAddress(@NonNull String bic) {
        String address = BIC_TO_ADDRESS.get(bic);
        if (address == null) {
            return Mono.error(new Exception(String.format("BIC %s doesn't have associated Ethereum address", bic)));
        }
        return Mono.just(address);
    }
}
