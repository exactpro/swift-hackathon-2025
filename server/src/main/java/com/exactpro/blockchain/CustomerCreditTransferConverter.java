package com.exactpro.blockchain;

import com.exactpro.blockchain.entity.Client;
import com.exactpro.blockchain.entity.Transfer;
import com.exactpro.blockchain.entity.TransferDetails;
import com.exactpro.blockchain.entity.TransferStatus;
import com.exactpro.iso20022.CustomerCreditTransfer;
import com.exactpro.iso20022.GroupHeader;
import com.exactpro.iso20022.Participant;
import com.exactpro.iso20022.TransactionInfo;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class CustomerCreditTransferConverter {

    public CustomerCreditTransfer convertFromClientAndTransferDetails(Client client, String clientBic, TransferDetails transferDetails) {
        GroupHeader groupHeader = GroupHeader.builder().messageId("").timestamp(Instant.now()).build();

        Participant debtor = Participant.builder()
            .fullName(client.getFullName())
            .iban(transferDetails.getDebtorIban()) // TODO Check that account belongs to the client
            .bic(clientBic)
            .build();

        Participant creditor = Participant.builder()
            .fullName(transferDetails.getCreditorFullName())
            .iban(transferDetails.getCreditorIban())
            .bic(transferDetails.getCreditorBic())
            .build();

        TransactionInfo transactionInfo = TransactionInfo.builder()
            .endToEndId(String.valueOf(UUID.randomUUID()))
            .currency(transferDetails.getCurrencyCode())
            .amount(transferDetails.getAmount())
            .settlementDate(LocalDate.now())
            .debtor(debtor)
            .creditor(creditor)
            .remittanceInfo("")
            .build();

        return new CustomerCreditTransfer(
            groupHeader,
            Collections.singletonList(transactionInfo)
        );
    }

    public List<Transfer> convertToTransfer(CustomerCreditTransfer customerCreditTransfer, TransferStatus status) {
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
                    .messageId(messageId)
                    .transferTimestamp(transferTimestamp)
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
