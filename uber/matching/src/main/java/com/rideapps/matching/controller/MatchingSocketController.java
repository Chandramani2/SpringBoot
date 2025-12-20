package com.rideapps.matching.controller;


import com.rideapps.matching.dto.Request.UpdateLocationRequest;
import com.rideapps.matching.service.DriverLocationEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.Map;

// In matching service
@Controller
public class MatchingSocketController {

    @Autowired
    private DriverLocationEntryService driverLocationEntryService;

    @MessageMapping("/driver.updateLocation") // Drivers send to /app/driver.updateLocation
    public void receiveLocation(@Payload UpdateLocationRequest updateLocation) {
        // Logic to update DriverLocationStore with the new coordinates
        System.out.println("Received Location Update for Driver: " + updateLocation.getDriverId());
        driverLocationEntryService.saveEntry(updateLocation);
    }


    @MessageMapping("/update-status")
    public void handleStatusChange(@Payload Map<String, Object> statusUpdate) {
        System.out.println("Driver status changed: " + statusUpdate.get("status"));
        // Perform specific logic for status change
    }
}