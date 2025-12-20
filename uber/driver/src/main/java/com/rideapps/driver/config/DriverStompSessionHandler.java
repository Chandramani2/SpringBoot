package com.rideapps.driver.config;


import com.rideapps.common.model.dto.Request.AcceptRideRequest;
import com.rideapps.driver.callback.RideAssignmentListener;
import com.rideapps.driver.service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.lang.reflect.Type;
import java.util.Map;

@Component
public class DriverStompSessionHandler extends StompSessionHandlerAdapter {
    @Autowired
    private ObjectMapper objectMapper;

    private final RideAssignmentListener rideAssignmentListener;

    // Inject via constructor
    public DriverStompSessionHandler(ObjectMapper objectMapper, RideAssignmentListener rideAssignmentListener) {
        this.rideAssignmentListener = rideAssignmentListener;
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {

        // Operation A: Listen for new rides
        session.subscribe("/topic/ride-assignments", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                // Tell Spring to convert the incoming message to a Map (or your specific DTO)
                return Map.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                // Logic to alert the driver about a new ride
                AcceptRideRequest rideRequest = objectMapper.convertValue(payload, AcceptRideRequest.class);
                rideAssignmentListener.onRideAssigned(rideRequest);
            }
        });

        // Operation B: Listen for system-wide announcements or surge pricing alerts
        session.subscribe("/topic/system-alerts", new StompFrameHandler() {

            @Override
            public Type getPayloadType(StompHeaders headers) {
                return String.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                // Logic to show a notification to the driver
            }
        });
    }
}