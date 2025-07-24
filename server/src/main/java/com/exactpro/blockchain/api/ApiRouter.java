package com.exactpro.blockchain.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class ApiRouter {

    @Bean
    public RouterFunction<ServerResponse> apiRoutes(ApiHandler apiHandler) {
        return route(GET("/api"), apiHandler::foobar);
    }
}
