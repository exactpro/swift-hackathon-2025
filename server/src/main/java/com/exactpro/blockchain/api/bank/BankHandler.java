package com.exactpro.blockchain.api.bank;

import com.exactpro.blockchain.entity.Transfer;
import com.exactpro.blockchain.repository.TransferRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class BankHandler {
    private final TransferRepository transferRepository;

    public BankHandler(TransferRepository transferRepository) {
        this.transferRepository = transferRepository;
    }

    public Mono<ServerResponse> getAllTransfers(ServerRequest request) {
        return ServerResponse.ok().body(transferRepository.findAll(), Transfer.class);
    }
}
