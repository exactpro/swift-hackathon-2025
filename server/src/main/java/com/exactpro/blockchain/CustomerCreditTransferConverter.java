package com.exactpro.blockchain;

import com.exactpro.blockchain.entity.Transfer;
import com.exactpro.blockchain.entity.TransferStatus;
import com.exactpro.iso20022.CustomerCreditTransfer;
import com.exactpro.iso20022.Participant;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CustomerCreditTransferConverter {
    public List<Transfer> convert(CustomerCreditTransfer customerCreditTransfer, TransferStatus status) {
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
