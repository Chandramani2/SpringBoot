package com.rideapps.matching.service;

import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class PathfindingService {

    public List<int[]> findPath(int[][] grid, int startX, int startY, int endX, int endY) {
        List<int[]> path = new ArrayList<>();
        boolean[][] visited = new boolean[grid.length][grid[0].length];
        if (dfs(grid, startX, startY, endX, endY, visited, path)) {
            return path;
        }
        return Collections.emptyList();
    }

    private boolean dfs(int[][] grid, int x, int y, int targetX, int targetY, boolean[][] visited, List<int[]> path) {
        // Boundary and obstacle checks
        if (x < 0 || y < 0 || x >= grid.length || y >= grid[0].length || grid[x][y] == 1 || visited[x][y]) {
            return false;
        }

        visited[x][y] = true;
        path.add(new int[]{x, y});


        if (x == targetX && y == targetY) {
            return true;
        }

        // Standard directions: Right, Down, Left, Up
        int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
        for (int[] dir : directions) {
            if (dfs(grid, x + dir[0], y + dir[1], targetX, targetY, visited, path)) {
                return true;
            }
        }

        // Backtrack if target not found in this branch
        path.remove(path.size() - 1);
        return false;
    }
}