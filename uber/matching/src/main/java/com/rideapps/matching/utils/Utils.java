package com.rideapps.matching.utils;

import com.rideapps.common.model.dto.Request.AcceptRideRequest;
import com.rideapps.common.model.dto.Request.RideParam;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class Utils {

    public Map<String, Object>  payloadToAcceptRideRequestMap(RideParam rideDetails, Long driverId) {
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
}
