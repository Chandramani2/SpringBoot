package com.rideapps.matching.controller;

import com.rideapps.common.model.dto.Request.CreateRideRequest;
import com.rideapps.common.model.enums.RideStatus;
import com.rideapps.common.model.enums.Status;
import com.rideapps.matching.Repository.DriverLocationRepository;
import com.rideapps.matching.dto.Request.UpdateLocationRequest;
import com.rideapps.matching.service.ClosestDriverService;
import com.rideapps.matching.service.PathfindingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@RestController
@RequestMapping("/v1/matching")
public class MatchingController {

    @Autowired
    private DriverLocationRepository driverLocationRepository;

    @Autowired
    private PathfindingService pathfindingService;

    @Autowired
    private ClosestDriverService closestDriverService;

    @Autowired
    private int[][] sharedGrid; // Injects the bean from MatchingApplication

    // Offsets to handle negative coordinates (e.g., Lat -90 to 90, Lon -180 to 180)
    private static final int LAT_OFFSET = 90;
    private static final int LON_OFFSET = 180;

    @PostMapping("/find-driver")
    public Map<String, Object> matchDriverAndCalculatePath(CreateRideRequest request) {
        // 1. Fetch only drivers with status AVAILABLE
        List<UpdateLocationRequest> availableDrivers = driverLocationRepository.findByStatus(Status.AVAILABLE);

        if (availableDrivers.isEmpty()) {
            throw new NoSuchElementException("No available drivers found at the moment");
        }

        // 2. Find the closest driver using Manhattan distance
        UpdateLocationRequest closestDriver = closestDriverService.findClosestDriver(availableDrivers, request);

        // 3. Extract coordinates
        // Normalize coordinates to positive grid indices
        int drvX = (int) (closestDriver.getLatitude() + LAT_OFFSET);
        int drvY = (int) (closestDriver.getLongitude() + LON_OFFSET);

        int pickX = (int) (request.getPickupLatitude() + LAT_OFFSET);
        int pickY = (int) (request.getPickupLongitude() + LON_OFFSET);

        int destX = (int) (request.getDestinationLatitude() + LAT_OFFSET);
        int destY = (int) (request.getDestinationLongitude() + LON_OFFSET);

        // 4. Calculate Leg 1: Driver to Pickup
        List<int[]> toPickup = pathfindingService.findPath(sharedGrid, drvX, drvY, pickX, pickY);

        // 5. Calculate Leg 2: Pickup to Destination
        List<int[]> toDestination = pathfindingService.findPath(sharedGrid, pickX, pickY, destX, destY);

        // 6. Construct response map
        Map<String, Object> result = new HashMap<>();
        result.put("driverId", closestDriver.getDriverId());
        result.put("driverStatus", closestDriver.getStatus());
        result.put("path_to_pickup", toPickup);
        result.put("path_to_destination", toDestination);

        return result;
    }
}
