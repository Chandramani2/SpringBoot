package com.rideapps.matching.strategy;

import com.rideapps.common.model.dto.Request.RideParam;
import com.rideapps.common.model.enums.RideStatus;
import com.rideapps.matching.Repository.DriverLocationRepository;
import com.rideapps.matching.dto.Request.UpdateLocationRequest;
import com.rideapps.matching.service.SendMessagesToTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class PathfindingActionRunner {

    @Autowired
    private DriverLocationRepository repository;

    @Autowired
    private SendMessagesToTopic sendMessagesToTopic;

    /**
     * Executes the side effects of a pathfinding step:
     * 1. Sends WebSocket updates every 2 seconds.
     * 2. Persists location to the database every 10 seconds or at the destination.
     */
    public void performStepActions(int x, int y, Long driverId, RideParam rideParam,boolean pickUp, 
                                   AtomicInteger stepCount, int targetX, int targetY) {
        try {
            // Frequency 1: Every 2 seconds - WebSocket Update
            Thread.sleep(2000);
            int currentStep = stepCount.incrementAndGet();

            // Convert grid indices back to real-world coordinates
            double realX = (double) x - 90;
            double realY = (double) y - 180;

            sendMessagesToTopic.sendLocationUpdate(realX, realY, driverId, rideParam.getRideStatus());

            // Frequency 2: Every 10 seconds (every 5th step) or at destination - Database Save
            boolean destinationReached = (x == targetX && y == targetY);

            if (currentStep % 5 == 0 || destinationReached) {
                UpdateLocationRequest loc = repository.findByDriverId(driverId);
                if (loc != null) {
                    loc.setLatitude(realX);
                    loc.setLongitude(realY);

                    if (!pickUp && destinationReached) {
                        loc.setRideStatus(RideStatus.COMPLETED);
                    }

                    repository.save(loc);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}