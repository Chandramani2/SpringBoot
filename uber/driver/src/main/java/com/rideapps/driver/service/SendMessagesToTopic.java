package com.rideapps.driver.service;

import com.rideapps.common.model.dto.Request.RideParam;
import com.rideapps.driver.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

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
        messagingTemplate.convertAndSend("/topic/rider-driver", (Object) payload);
    }

    public void createTripAndCalculateFare(RideParam rideDetails, Long driverId) {
        Map<String, Object> payload = utils.tripRideParam(rideDetails, driverId);
        payload.put("rideCompleted", true);
        // Push through the WebSocket broker.
        // The Driver Service (acting as a client) will receive this instantly.
        messagingTemplate.convertAndSend("/topic/rider-driver", (Object) payload);
    }
}
