package com.rideapps.matching.service;

import com.rideapps.common.model.dto.Request.RideParam;
import com.rideapps.common.model.enums.RideStatus;
import com.rideapps.matching.Repository.DriverLocationRepository;
import com.rideapps.matching.dto.Request.UpdateLocationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class PathfindingService {

    @Autowired
    private DriverLocationRepository repository;

    @Autowired
    private SendMessagesToTopic sendMessagesToTopic;

    @Async
    public void simulateRide(int[][] grid, int startX, int startY, int endX, int endY,
                             Long driverId, RideParam rideParam) {
        boolean[][] visited = new boolean[grid.length][grid[0].length];
        AtomicInteger stepCount = new AtomicInteger(0);

        // Start recursive simulation without pre-calculating the list
        dfsSimulate(grid, startX, startY, endX, endY, visited, driverId, rideParam, stepCount);
    }

    private boolean dfsSimulate(int[][] grid, int x, int y, int targetX, int targetY,
                                boolean[][] visited, Long driverId, RideParam rideParam, AtomicInteger stepCount) {

        // 1. Validation and Boundary Checks
        if (x < 0 || y < 0 || x >= grid.length || y >= grid[0].length || grid[x][y] == 1 || visited[x][y]) {
            return false;
        }

        // 2. Perform Simulation "Step" actions immediately
        visited[x][y] = true;
        performStepActions(x, y, driverId, rideParam, stepCount, targetX, targetY);

        // 3. Check for destination
        if (x == targetX && y == targetY) {
            rideParam.setRideStatus(RideStatus.COMPLETED);
            sendTripCompletionToDriver(rideParam, driverId);
            return true;
        }

        // 4. Move toward target
        List<int[]> sortedDirections = getSortedDirections(x, y, targetX, targetY);

        for (int[] dir : sortedDirections) {
            if (dfsSimulate(grid, x + dir[0], y + dir[1], targetX, targetY, visited, driverId, rideParam, stepCount)) {
                return true;
            }
        }

        return false;
    }

    private void performStepActions(int x, int y, Long driverId, RideParam rideParam,
                                    AtomicInteger stepCount, int targetX, int targetY) {
        try {
            // Frequency 1: Every 2 seconds - WebSocket Update
            Thread.sleep(2000);
            int currentStep = stepCount.incrementAndGet();

            double realX = (double) x - 90;
            double realY = (double) y - 180;
            // Assuming messagingTemplate is available or handled via sendMessagesToTopic
            sendMessagesToTopic.sendLocationUpdate(realX,realY,driverId, rideParam.getRideStatus());

            // Frequency 2: Every 10 seconds (every 5th step) - Database Save
            boolean destinationReached = x == targetX && y == targetY;
            if (currentStep % 5 == 0 || destinationReached) {
                UpdateLocationRequest loc = repository.findByDriverId(driverId);
                if (loc != null) {
                    loc.setLatitude(realX);
                    loc.setLongitude(realY);
                    repository.save(loc);
                }
                if(destinationReached){
                    loc.setLatitude(realX);
                    loc.setLongitude(realY);
                    loc.setRideStatus(RideStatus.COMPLETED);
                    repository.save(loc);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void sendTripCompletionToDriver(RideParam rideParam, Long driverId) {
        sendMessagesToTopic.createTripAndCalculateFare(rideParam, driverId);
    }

    private List<int[]> getSortedDirections(int x, int y, int targetX, int targetY) {
        List<int[]> directions = new ArrayList<>(Arrays.asList(
                new int[]{0, 1}, new int[]{1, 0}, new int[]{0, -1}, new int[]{-1, 0}
        ));
        directions.sort(Comparator.comparingInt(dir ->
                Math.abs((x + dir[0]) - targetX) + Math.abs((y + dir[1]) - targetY)
        ));
        return directions;
    }
}