package com.rideapps.matching.strategy;

import com.rideapps.common.model.dto.Request.RideParam;
import com.rideapps.common.model.enums.RideStatus;
import com.rideapps.matching.service.SendMessagesToTopic;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class DfsPathfindingStrategy implements PathfindingStrategy {
    private final SendMessagesToTopic messenger;
    private final PathfindingActionRunner actionRunner;

    public DfsPathfindingStrategy(SendMessagesToTopic messenger, PathfindingActionRunner actionRunner) {
        this.messenger = messenger;
        this.actionRunner = actionRunner;
    }

    @Override
    public void simulate(int[][] grid, int startX, int startY, int endX, int endY, Long driverId, boolean pickUp, RideParam rideParam) {
        boolean[][] visited = new boolean[grid.length][grid[0].length];
        dfs(grid, startX, startY, endX, endY, visited, driverId, rideParam, pickUp, new AtomicInteger(0));
    }

    private boolean dfs(int[][] grid, int x, int y, int tx, int ty, boolean[][] v, Long dId, RideParam rp, boolean pickUp, AtomicInteger step) {
        if (x < 0 || y < 0 || x >= grid.length || y >= grid[0].length || grid[x][y] == 1 || v[x][y]) return false;

        v[x][y] = true;
        actionRunner.performStepActions(x, y, dId, rp, pickUp, step, tx, ty);

        if (x == tx && y == ty) {
            if(!pickUp) rp.setRideStatus(RideStatus.COMPLETED);
            messenger.createTripAndCalculateFare(rp, dId);
            return true;
        }

        int[][] dirs = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
        for (int[] dir : dirs) {
            if (dfs(grid, x + dir[0], y + dir[1], tx, ty, v, dId, rp, pickUp, step)) return true;
        }
        return false;
    }
}