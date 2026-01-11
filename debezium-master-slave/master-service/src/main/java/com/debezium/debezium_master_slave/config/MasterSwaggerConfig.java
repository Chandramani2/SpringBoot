package com.debezium.debezium_master_slave.config;

import org.springdoc.core.models.GroupedOpenApi;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MasterSwaggerConfig {

    @Bean
    public GroupedOpenApi groupedOpenApi() {
        return GroupedOpenApi.builder()
                .group("debezium-master-postgres")
                .packagesToScan("com.debezium.debezium_master_slave.controller")
                .build();
    }
}
