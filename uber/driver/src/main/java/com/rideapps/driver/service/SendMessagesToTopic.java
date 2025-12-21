package com.rideapps.driver.service;

import com.rideapps.common.model.dto.Request.RideParam;
import com.rideapps.driver.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

@Service
public class SendMessagesToTopic {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private Utils utils;

    @Autowired
    private ObjectMapper objectMapper;

    public void sendRideStatusToRider(Map<String, Object> payload) {
        payload.put("rideStatusChanged", true);
        // Push through the WebSocket broker.
        // The Rider Service (acting as a client) will receive this instantly.
        messagingTemplate.convertAndSend("/topic/ride-update-status", (Object) payload);
    }

    public void sendDriverLocationToRider(Map<String, Object> payload) {
        payload.put("driverLocationDetails", true);
        // Push through the WebSocket broker.
        // The Rider Service (acting as a client) will receive this instantly.
        messagingTemplate.convertAndSend("/topic/driver-rider-location", (Object) payload);
    }

    public void driverReachedLocation(Map<String, Object> payload) {
        payload.put("driverReachedLocation", true);
        // Push through the WebSocket broker.
        // The Rider Service (acting as a client) will receive this instantly.
        messagingTemplate.convertAndSend("/topic/driver-reached-location", (Object) payload);
    }
}
