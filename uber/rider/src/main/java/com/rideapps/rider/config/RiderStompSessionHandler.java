package com.rideapps.rider.config;


//import com.rideapps.driver.callback.RideAssignmentListener;
import com.rideapps.rider.callback.DriverUpdateListener;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.Map;

@Component
public class RiderStompSessionHandler extends StompSessionHandlerAdapter {


    private final DriverUpdateListener driverUpdateListener;

    // Inject via constructor
    public RiderStompSessionHandler(DriverUpdateListener driverUpdateListener) {
        this.driverUpdateListener = driverUpdateListener;
    }

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {

        // Operation A: Listen for new rides
        session.subscribe("/topic/ride-update-status", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                // Tell Spring to convert the incoming message to a Map (or your specific DTO)
                return Map.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                // Logic to alert the driver about a new ride
                driverUpdateListener.updateRideStatus((Map<String, Object>) payload);
            }
        });

        // Operation B: Listen for system-wide announcements or surge pricing alerts
        session.subscribe("/topic/driver-rider-location", new StompFrameHandler() {

            @Override
            public Type getPayloadType(StompHeaders headers) {
                return Map.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                // Logic to show a notification to the driver
                driverUpdateListener.driverLocationUpdate((Map<String, Object>) payload);
            }
        });

        // Operation C: Listen for If Driver Reached at pickUp or Destination
        session.subscribe("/topic/driver-reached-location", new StompFrameHandler() {

            @Override
            public Type getPayloadType(StompHeaders headers) {
                return Map.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                // Logic to show a notification to the driver
                driverUpdateListener.getDriverLocation((Map<String, Object>) payload);
            }
        });
    }
}