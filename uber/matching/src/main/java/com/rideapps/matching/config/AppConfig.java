package com.rideapps.matching.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;


@Configuration
public class AppConfig {

    // Option A: Simple definition
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}