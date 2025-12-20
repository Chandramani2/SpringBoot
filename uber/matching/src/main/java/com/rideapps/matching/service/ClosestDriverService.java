package com.rideapps.matching.service;

import com.rideapps.common.model.dto.Request.CreateRideRequest;
import com.rideapps.common.model.dto.Request.RideParam;
import com.rideapps.matching.dto.Request.UpdateLocationRequest;
import com.rideapps.matching.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ClosestDriverService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private Utils utils;

    public UpdateLocationRequest findClosestDriver(List<UpdateLocationRequest> drivers, RideParam request) {
        UpdateLocationRequest closest = null;
        double minDistance = Double.MAX_VALUE;

        int pickX = (int) request.getPickUp().getLatitude();
        int pickY = (int) request.getPickUp().getLongitude();

        for (UpdateLocationRequest driver : drivers) {
            int drvX = (int) driver.getLatitude();
            int drvY = (int) driver.getLongitude();

            // Manhattan Distance logic
            double distance = Math.abs(drvX - pickX) + Math.abs(drvY - pickY);

            if (distance < minDistance) {
                minDistance = distance;
                closest = driver;
            }
        }
        return closest;
    }

    public void acceptRide(RideParam rideDetails, Long driverId) {
        Map<String, Object> payload = utils.payloadToAcceptRideRequestMap(rideDetails, driverId);
        payload.put("message", "New Ride Available! ");
        // Push through the WebSocket broker.
        // The Driver Service (acting as a client) will receive this instantly.
        messagingTemplate.convertAndSend("/topic/ride-assignments", (Object) payload);
    }
}
