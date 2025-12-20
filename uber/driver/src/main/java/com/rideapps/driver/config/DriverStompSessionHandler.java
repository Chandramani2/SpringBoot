package com.rideapps.driver.config;


import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import java.lang.reflect.Type;

public class DriverStompSessionHandler extends StompSessionHandlerAdapter {

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        System.out.println("Connected to Matching Service. Session ID: " + session.getSessionId());

        // 1. Subscribe to ride requests from the matching service
        session.subscribe("/topic/update-driver-location", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return String.class; // Or your RideRequest DTO
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                System.out.println("Received Ride Request: " + payload);
            }
        });
    }

    @Override
    public void handleException(StompSession session, org.springframework.messaging.simp.stomp.StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
        exception.printStackTrace();
    }
}