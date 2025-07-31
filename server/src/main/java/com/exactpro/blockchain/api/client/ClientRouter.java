package com.exactpro.blockchain.api.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class ClientRouter {
    @Value("${content.path}")
    private String contentPath;
    @Bean
    public @NonNull RouterFunction<ServerResponse> clientRoutes(@NonNull ClientHandler clientHandler) {
        return route(GET(contentPath + "/api/client/account"), clientHandler::getAccountsByClientId)
            .andRoute(GET(contentPath + "/api/client/transfer"), clientHandler::getTransfersByClientId)
            .andRoute(POST(contentPath + "/api/client/transfer"), clientHandler::transfer);
    }
}
