package com.rideapps.driver.utils;

import com.rideapps.common.model.dto.Request.RideParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

@Service
public class Utils {

    @Autowired
    private ObjectMapper objectMapper;

    public Map<String, Object>  acceptRideParam(RideParam rideDetails, Long driverId) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("driverId", driverId);
        payload.put("riderId", rideDetails.getRiderId());
        payload.put("pickUp", rideDetails.getPickUp());
        payload.put("rideStatus", rideDetails.getRideStatus());
        payload.put("destination", rideDetails.getDestination());
        payload.put("paymentMethod", rideDetails.getPaymentMethod());
        payload.put("estimatedFare", rideDetails.getEstimatedFare());

        return payload;
    }

    public Map<String, Object>  tripRideParam(RideParam rideDetails, Long driverId) {
        // 1. Convert the object to a Map
        Map<String, Object> payload = objectMapper.convertValue(rideDetails, Map.class);

        // 2. Now you can add extra key-value pairs
        payload.put("driverId", driverId);
        return payload;
    }
}
