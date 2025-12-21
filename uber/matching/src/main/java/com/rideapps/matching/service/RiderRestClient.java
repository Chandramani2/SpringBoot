package com.rideapps.matching.service;

import com.rideapps.common.model.dto.Request.RideParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.Map;

public class RiderRestClient {

    @Value("${rider.service.url}")
    private String riderServiceUrl;

    @Autowired
    private RestClient riderServiceClient;


    public Map<String, Object> initiateTripAndFareCalculation(RideParam request) {
        try {
            String url = riderServiceUrl + "/v1/trips/" + request.getRideId() + "/end";
            return riderServiceClient.post()
                    .uri(url)
                    .body(request)
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            Map<String, Object> errorMap = new HashMap<>();
            // Only the exception message string is stored
            errorMap.put("error", e.getMessage());
            throw new RuntimeException("Ride Not Found: " + errorMap);
        }
    }
}
