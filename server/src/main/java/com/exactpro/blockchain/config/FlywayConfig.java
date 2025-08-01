package com.exactpro.blockchain.config;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlywayConfig {
    @Value("${spring.datasource.url}")
    private String flywayUrl;

    @Value("${spring.datasource.username}")
    private String flywayUser;

    @Value("${spring.datasource.password}")
    private String flywayPassword;

    @Bean(initMethod = "migrate")
    public Flyway flyway() {
        return Flyway.configure()
            .dataSource(flywayUrl, flywayUser, flywayPassword)
            .validateMigrationNaming(true)
            .load();
    }
}
