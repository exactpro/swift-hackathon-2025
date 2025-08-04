package com.exactpro.blockchain;

import com.exactpro.blockchain.entity.*;
import com.exactpro.iso20022.CustomerCreditTransfer;
import com.exactpro.iso20022.GroupHeader;
import com.exactpro.iso20022.Participant;
import com.exactpro.iso20022.TransactionInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CustomerCreditTransferConverter {
    private final IdGenerator idGenerator = new IdGenerator();

    @Value("${client.bic}")
    private String myBic;

    public Transfer newTransfer(@NonNull Client client, @NonNull TransferRequest transferRequest) {
        Instant now = Instant.now();
        Transfer.Builder transferBuilder = Transfer.builder();
        transferBuilder
            .amount(transferRequest.getAmount())
            .clientId(client.getClientId())
            .creditorBic(transferRequest.getCreditorBic())
            .creditorFullName(transferRequest.getCreditorFullName())
            .creditorIban(transferRequest.getCreditorIban())
            .currencyCode(transferRequest.getCurrencyCode())
            .debtorBic(myBic)
            .debtorFullName(client.getFullName())
            .debtorIban(transferRequest.getDebtorIban())
            .endToEndId(idGenerator.generateId(16))
            .remittanceInfo(transferRequest.getComment())
            .settlementDate(now.atZone(ZoneId.systemDefault()).toLocalDate())
            .status(TransferStatus.PENDING)
            .transferTimestamp(now);
        return transferBuilder.build();
    }

    public CustomerCreditTransfer toCustomerCreditTransfer(@NonNull Transfer transfer) {
        GroupHeader groupHeader = GroupHeader.builder()
            .messageId(idGenerator.generateId(16))
            .timestamp(transfer.getTransferTimestamp())
            .build();

        Participant debtor = Participant.builder()
            .fullName(transfer.getDebtorFullName())
            .iban(transfer.getDebtorIban()) // TODO Check that account belongs to the client
            .bic(transfer.getDebtorBic())
            .build();

        Participant creditor = Participant.builder()
            .fullName(transfer.getCreditorFullName())
            .iban(transfer.getCreditorIban())
            .bic(transfer.getCreditorBic())
            .build();

        TransactionInfo transactionInfo = TransactionInfo.builder()
            .endToEndId(transfer.getEndToEndId())
            .currency(transfer.getCurrencyCode())
            .amount(transfer.getAmount())
            .settlementDate(transfer.getSettlementDate())
            .debtor(debtor)
            .creditor(creditor)
            .remittanceInfo(transfer.getRemittanceInfo())
            .build();

        return new CustomerCreditTransfer(
            groupHeader,
            Collections.singletonList(transactionInfo)
        );
    }

    public List<Transfer> toTransfer(CustomerCreditTransfer customerCreditTransfer, TransferStatus status) {
        if (customerCreditTransfer == null || customerCreditTransfer.getTransactionInfos().isEmpty()) {
            return Collections.emptyList();
        }

        String messageId = customerCreditTransfer.getGroupHeader().getMessageId();
        Instant transferTimestamp = customerCreditTransfer.getGroupHeader().getTimestamp();

        return customerCreditTransfer.getTransactionInfos().stream()
            .map(transactionInfo -> {
                Participant debtor = transactionInfo.getDebtor();
                Participant creditor = transactionInfo.getCreditor();

                return Transfer.builder()
                    .status(status)
                    .transferTimestamp(transferTimestamp)
                    .endToEndId(transactionInfo.getEndToEndId())
                    .currencyCode(transactionInfo.getCurrency())
                    .amount(transactionInfo.getAmount())
                    .settlementDate(transactionInfo.getSettlementDate())
                    .debtorFullName(debtor.getFullName())
                    .debtorIban(debtor.getIban())
                    .debtorBic(debtor.getBic())
                    .creditorFullName(creditor.getFullName())
                    .creditorIban(creditor.getIban())
                    .creditorBic(creditor.getBic())
                    .remittanceInfo(transactionInfo.getRemittanceInfo())
                    .build();
            })
            .collect(Collectors.toList());
    }

    public List<Pacs008Message> toPacs008Message(CustomerCreditTransfer customerCreditTransfer) {
        String messageId = customerCreditTransfer.getGroupHeader().getMessageId();
        Instant transferTimestamp = customerCreditTransfer.getGroupHeader().getTimestamp();

        return customerCreditTransfer.getTransactionInfos().stream()
            .map(transactionInfo -> {
                Participant debtor = transactionInfo.getDebtor();
                Participant creditor = transactionInfo.getCreditor();

                return Pacs008Message.builder()
                    .messageId(messageId)
                    .timestamp(transferTimestamp)
                    .endToEndId(transactionInfo.getEndToEndId())
                    .currency(transactionInfo.getCurrency())
                    .amount(transactionInfo.getAmount())
                    .settlementDate(transactionInfo.getSettlementDate())
                    .debtorFullName(debtor.getFullName())
                    .debtorIban(debtor.getIban())
                    .debtorBic(debtor.getBic())
                    .creditorFullName(creditor.getFullName())
                    .creditorIban(creditor.getIban())
                    .creditorBic(creditor.getBic())
                    .remittanceInfo(transactionInfo.getRemittanceInfo())
                    .build();
            })
            .collect(Collectors.toList());
    }
}
