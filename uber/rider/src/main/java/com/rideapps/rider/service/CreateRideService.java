package com.rideapps.rider.service;

import com.rideapps.common.model.dto.Request.CreateRideRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.Map;


@Component
public class CreateRideService {

    @Value("${matching.service.url}")
    private String matchingServiceUrl;

    @Autowired
    private RestClient matchingServiceClient;


    public Map<String, Object> initiateMatching(CreateRideRequest request) {
        try {
            return matchingServiceClient.post()
                    .uri("/v1/matching/find-driver")
                    .body(request)
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            Map<String, Object> errorMap = new HashMap<>();
            // Only the exception message string is stored
            errorMap.put("error", e.getMessage());
            return errorMap;
        }
    }
}