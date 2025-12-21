package com.rideapps.matching.service;

import com.rideapps.common.model.dto.Request.RideParam;
import com.rideapps.common.model.enums.RideStatus;
import com.rideapps.matching.Repository.DriverLocationRepository;
import com.rideapps.matching.dto.Request.UpdateLocationRequest;
import com.rideapps.matching.strategy.AStarPathfindingStrategy;
import com.rideapps.matching.strategy.PathfindingActionRunner;
import com.rideapps.matching.strategy.PathfindingStrategy;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class PathfindingService {

    @Autowired
    private SendMessagesToTopic sendMessagesToTopic;

    @Autowired
    private PathfindingActionRunner actionRunner;

    private PathfindingStrategy strategy;

    @PostConstruct
    public void init() {
        // Use A* by default for optimization
        this.strategy = new AStarPathfindingStrategy(sendMessagesToTopic, actionRunner);
    }

    @Async
    public void simulateRide(int[][] grid, int startX, int startY, int endX, int endY,
                             Long driverId, boolean pickUp, RideParam rideParam) {
        // The Controller still calls this same method, but it now executes A* logic
        strategy.simulate(grid, startX, startY, endX, endY, driverId, pickUp, rideParam);
    }

}