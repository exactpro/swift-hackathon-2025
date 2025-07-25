package com.exactpro.blockchain.api.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class ClientRouter {

    @Bean
    public RouterFunction<ServerResponse> clientRoutes(ClientHandler clientHandler) {
        return route(GET("/api/client/{clientId}/account"), clientHandler::getAccountsByClientId);
    }
}
