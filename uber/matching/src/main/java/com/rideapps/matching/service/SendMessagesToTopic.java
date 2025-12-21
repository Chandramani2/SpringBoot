package com.rideapps.matching.service;

import com.rideapps.common.model.dto.Request.RideParam;
import com.rideapps.common.model.enums.RideStatus;
import com.rideapps.matching.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class SendMessagesToTopic {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private Utils utils;

    public void acceptRide(RideParam rideDetails, Long driverId) {
        Map<String, Object> payload = utils.acceptRideParam(rideDetails, driverId);
        payload.put("acceptRide", "New Ride Available! ");
        // Push through the WebSocket broker.
        // The Driver Service (acting as a client) will receive this instantly.
        messagingTemplate.convertAndSend("/topic/ride-assignments", (Object) payload);
    }

    public void createTripAndCalculateFare(RideParam rideDetails, Long driverId) {
        Map<String, Object> payload = utils.tripRideParam(rideDetails, driverId);
        payload.put("rideCompleted", true);
        // Push through the WebSocket broker.
        // The Driver Service (acting as a client) will receive this instantly.
        messagingTemplate.convertAndSend("/topic/ride-assignments", (Object) payload);
    }

    public void sendLocationUpdate(Double latitude, Double longitude, Long driverId, RideStatus rideStatus) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("driverId", driverId);
        payload.put("latitude", latitude);
        payload.put("longitude", longitude);
        payload.put("rideStatus", rideStatus);
        payload.put("driverLocation", true);
        // Push through the WebSocket broker.
        // The Driver Service (acting as a client) will receive this instantly.
        messagingTemplate.convertAndSend("/topic/driver-matching-update", (Object) payload);
    }

    public void sendUpdatedRideStatus( RideParam rideDetails, Long driverId) {
        Map<String, Object> payload = utils.tripRideParam(rideDetails, driverId);
        payload.put("driverId", driverId);
        payload.put("driverStatus", rideDetails.getRideStatus());
        // Push through the WebSocket broker.
        // The Driver Service (acting as a client) will receive this instantly.
        messagingTemplate.convertAndSend("/topic/driver-matching-update", (Object) payload);
    }

    public void reachedAtPickup( RideParam rideDetails, Long driverId) {
        Map<String, Object> payload = utils.tripRideParam(rideDetails, driverId);
        payload.put("driverId", driverId);
        payload.put("reachedPickup", true);
        // Push through the WebSocket broker.
        // The Driver Service (acting as a client) will receive this instantly.
        messagingTemplate.convertAndSend("/topic/driver-reached-location", (Object) payload);
    }
    public void reachedAtDestination( RideParam rideDetails, Long driverId) {
        Map<String, Object> payload = utils.tripRideParam(rideDetails, driverId);
        payload.put("driverId", driverId);
        payload.put("reachedDestination", true);
        // Push through the WebSocket broker.
        // The Driver Service (acting as a client) will receive this instantly.
        messagingTemplate.convertAndSend("/topic/driver-reached-location", (Object) payload);
    }
}
