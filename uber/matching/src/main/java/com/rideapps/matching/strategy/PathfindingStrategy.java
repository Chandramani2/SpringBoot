package com.rideapps.matching.strategy;

import com.rideapps.common.model.dto.Request.RideParam;

public interface PathfindingStrategy {
    void simulate(int[][] grid, int startX, int startY, int endX, int endY,
                  Long driverId, boolean pickUp, RideParam rideParam);
}