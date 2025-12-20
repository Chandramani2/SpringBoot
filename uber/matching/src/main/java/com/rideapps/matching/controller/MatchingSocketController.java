package com.rideapps.matching.controller;


import com.rideapps.matching.dto.Request.UpdateLocationRequest;
import com.rideapps.matching.service.DriverLocationEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

// In matching service
@Controller
public class MatchingSocketController {

    @Autowired
    private DriverLocationEntryService driverLocationEntryService;

    @MessageMapping("/driver.updateLocation") // Drivers send to /app/driver.updateLocation
    @SendTo("/topic/update-driver-location")
    public void receiveLocation(@Payload UpdateLocationRequest updateLocation) {
        // Logic to update DriverLocationStore with the new coordinates
        System.out.println("Received Location Update for Driver: " + updateLocation.getDriverId());
        driverLocationEntryService.saveEntry(updateLocation);
    }


    @MessageMapping("/test.connection") // Destination: /app/test.connection
    @SendTo("/topic/test-replies")       // Response sent to: /topic/test-replies
    public String testConnection(String message) {
        System.out.println("DEBUG: WebSocket Test Received: " + message);
        return "Backend received: " + message;
    }
}