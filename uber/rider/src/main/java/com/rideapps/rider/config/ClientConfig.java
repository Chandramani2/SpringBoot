package com.rideapps.rider.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ClientConfig {

    @Value("${matching.service.url}")
    private String matchingServiceUrl;

    @Bean
    public RestClient matchingServiceClient() {
        return RestClient.builder()
                .baseUrl(matchingServiceUrl)
                .build();
    }
}