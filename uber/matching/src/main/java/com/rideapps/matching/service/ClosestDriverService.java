package com.rideapps.matching.service;

import com.rideapps.common.model.dto.Request.CreateRideRequest;
import com.rideapps.matching.dto.Request.UpdateLocationRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClosestDriverService {

    public UpdateLocationRequest findClosestDriver(List<UpdateLocationRequest> drivers, CreateRideRequest request) {
        UpdateLocationRequest closest = null;
        double minDistance = Double.MAX_VALUE;

        int pickX = (int) request.getPickupLatitude();
        int pickY = (int) request.getPickupLongitude();

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
}
