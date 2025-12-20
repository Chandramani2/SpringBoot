package com.rideapps.driver.config;


import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import java.lang.reflect.Type;
import java.util.Map;

public class DriverStompSessionHandler extends StompSessionHandlerAdapter {
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