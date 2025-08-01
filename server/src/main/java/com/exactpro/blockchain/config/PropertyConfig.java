package com.exactpro.blockchain.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.ClassPathResource;


@Configuration
public class PropertyConfig {
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();

        String configurationPath = System.getProperty("server.config");
        if (configurationPath == null) {
            configurer.setLocations(
                new ClassPathResource("application.properties") // src/main/resources
            );
        } else {
            configurer.setLocations(
                new FileSystemResource(configurationPath)
            );
        }

//        configurer.setIgnoreUnresolvablePlaceholders(true);
        return configurer;
    }
}