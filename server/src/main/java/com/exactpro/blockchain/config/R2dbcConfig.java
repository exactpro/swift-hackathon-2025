package com.exactpro.blockchain.config;

import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;

import static io.r2dbc.spi.ConnectionFactoryOptions.*;

@Configuration
@EnableR2dbcRepositories(basePackages = "com.exactpro.blockchain.repository")
public class R2dbcConfig {

    @Value("${spring.r2dbc.url}")
    private String r2dbcUrl;
    @Value("${spring.r2dbc.username}")
    private String username;
    @Value("${spring.r2dbc.password}")
    private String password;

    @Bean
    public ConnectionFactory connectionFactory() {
        ConnectionFactoryOptions options = ConnectionFactoryOptions.parse(r2dbcUrl)
                .mutate()
                .option(USER, username)
                .option(PASSWORD, password)
                .build();

        return ConnectionFactories.get(options);
    }

    @Bean
    public R2dbcEntityTemplate r2dbcEntityTemplate(ConnectionFactory connectionFactory) {
        return new R2dbcEntityTemplate(connectionFactory);
    }
}
