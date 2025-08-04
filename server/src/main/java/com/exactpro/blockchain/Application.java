package com.exactpro.blockchain;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.lang.NonNull;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.server.adapter.WebHttpHandlerBuilder;
import reactor.netty.http.server.HttpServer;

import java.time.Duration;

@Configuration
@ComponentScan(basePackages = "com.exactpro")
@EnableWebFlux
public class Application implements WebFluxConfigurer {
    private final static Logger logger = LogManager.getLogger(Application.class);

    @Value("${server.port:8081}")
    private int port;

    @Value("${server.host:0.0.0.0}")
    private String host;

    public static void main(@NonNull String[] args) {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Application.class)) {
            context.getBean(HttpServer.class).bindUntilJavaShutdown(Duration.ofSeconds(60), null);
        }
    }

    @Bean
    public @NonNull HttpServer httpServer(ApplicationContext context) {
        logger.info("Starting HTTP server {}:{}", host, port);
        HttpHandler handler = WebHttpHandlerBuilder.applicationContext(context).build();
        ReactorHttpHandlerAdapter adapter = new ReactorHttpHandlerAdapter(handler);
        return HttpServer.create()
                .host(this.host)
                .port(this.port)
                .handle(adapter);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("*")
                .allowedHeaders("*");
    }
}
