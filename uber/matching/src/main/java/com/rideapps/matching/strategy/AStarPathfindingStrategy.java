package com.rideapps.matching.strategy;

import com.rideapps.common.model.dto.Request.RideParam;
import com.rideapps.common.model.enums.RideStatus;
import com.rideapps.matching.service.SendMessagesToTopic;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class AStarPathfindingStrategy implements PathfindingStrategy {
    private final SendMessagesToTopic messenger;
    private final PathfindingActionRunner actionRunner;

    public AStarPathfindingStrategy(SendMessagesToTopic messenger, PathfindingActionRunner actionRunner) {
        this.messenger = messenger;
        this.actionRunner = actionRunner;
    }

    private static class Node implements Comparable<Node> {
        int x, y, g;
        double h;
        Node(int x, int y, int g, double h) { this.x = x; this.y = y; this.g = g; this.h = h; }
        double getF() { return g + h; }
        @Override public int compareTo(Node o) { return Double.compare(this.getF(), o.getF()); }
    }

    @Override
    public void simulate(int[][] grid, int startX, int startY, int endX, int endY, Long driverId, boolean pickUp, RideParam rideParam) {
        PriorityQueue<Node> openSet = new PriorityQueue<>();
        boolean[][] visited = new boolean[grid.length][grid[0].length];

        openSet.add(new Node(startX, startY, 0, Math.abs(startX - endX) + Math.abs(startY - endY)));

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();
            if (visited[current.x][current.y]) continue;
            visited[current.x][current.y] = true;

            actionRunner.performStepActions(current.x, current.y, driverId, rideParam, pickUp, new AtomicInteger(current.g), endX, endY);

            if (current.x == endX && current.y == endY) {
                if(!pickUp) rideParam.setRideStatus(RideStatus.COMPLETED);
                else rideParam.setRideStatus(RideStatus.STARTED);
                messenger.createTripAndCalculateFare(rideParam, driverId);
                return;
            }

            int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
            for (int[] dir : directions) {
                int nx = current.x + dir[0], ny = current.y + dir[1];
                if (nx >= 0 && ny >= 0 && nx < grid.length && ny < grid[0].length && grid[nx][ny] == 0 && !visited[nx][ny]) {
                    openSet.add(new Node(nx, ny, current.g + 1, Math.abs(nx - endX) + Math.abs(ny - endY)));
                }
            }
        }
    }
}