package com.exactpro.blockchain.api.bank;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class BankRouter {
    @Value("${content.path}")
    private String contentPath;

    @Bean
    public @NonNull RouterFunction<ServerResponse> bankRoutes(@NonNull BankHandler bankHandler) {
        return route(GET(contentPath + "/api/bank/transfer"), bankHandler::getAllTransfers)
            .andRoute(GET(contentPath + "/api/bank/transfer/{transferId}"), bankHandler::getTransfer)
            .andRoute(GET(contentPath + "/api/bank/transfer/{transferId}/message"), bankHandler::getMessages)
            .andRoute(GET(contentPath + "/api/bank/iban"), bankHandler::getAllIbans);
    }
}
