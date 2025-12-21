package com.rideapps.matching.controller;

import com.rideapps.common.model.dto.ApiResponse;
import com.rideapps.common.model.dto.Request.AcceptRideRequest;
import com.rideapps.common.model.dto.Request.CreateRideRequest;
import com.rideapps.common.model.dto.Request.RideParam;
import com.rideapps.common.model.enums.RideStatus;
import com.rideapps.common.model.enums.Status;
import com.rideapps.matching.Repository.DriverLocationRepository;
import com.rideapps.matching.dto.Request.UpdateLocationRequest;
import com.rideapps.matching.service.ClosestDriverService;
import com.rideapps.matching.service.PathfindingService;
import com.rideapps.matching.service.SendMessagesToTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
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
    private RestTemplate restTemplate;

    @Autowired
    private SendMessagesToTopic sendMessagesToTopic;

    @Value("${driver.service.url}")
    private String driverServiceUrl;

    @Autowired
    private int[][] sharedGrid; // Injects the bean from MatchingApplication

    // Offsets to handle negative coordinates (e.g., Lat -90 to 90, Lon -180 to 180)
    private static final int LAT_OFFSET = 90;
    private static final int LON_OFFSET = 180;

    @PostMapping("/find-driver")
    public Map<String, Object> matchDriverAndCalculatePath(@RequestBody RideParam request) {
        // 1. Fetch only drivers with status AVAILABLE
        List<UpdateLocationRequest> availableDrivers = driverLocationRepository.findByStatus(Status.AVAILABLE);

        if (availableDrivers.isEmpty()) {
            throw new NoSuchElementException("No available drivers found at the moment");
        }

        // 2. Find the closest driver using Manhattan distance
        UpdateLocationRequest closestDriver = closestDriverService.findClosestDriver(availableDrivers, request);

        //Async Topic
//        closestDriverService.acceptRide(request, closestDriver.getDriverId());

        // b. HTTP Request to Driver Service
        String url = driverServiceUrl + "/v1/drivers/" + closestDriver.getDriverId() + "/accept";
        AcceptRideRequest rideRequest = new AcceptRideRequest(); // Populate fields as needed

        ApiResponse<Boolean> response = restTemplate.postForObject(url, rideRequest, ApiResponse.class);


        if (response != null && response.getData()) {

            //set status to assigned as driver accepted ride
            request.setRideStatus(RideStatus.ASSIGNED);
            sendMessagesToTopic.sendUpdatedRideStatus(request, closestDriver.getDriverId());

            // 3. Extract coordinates
            // Normalize coordinates to positive grid indices
            // a). Normalize coordinates with offsets
            int drvX = (int) (closestDriver.getLatitude() + LAT_OFFSET);
            int drvY = (int) (closestDriver.getLongitude() + LON_OFFSET);

            int pickX = (int) (request.getPickUp().getLatitude() + LAT_OFFSET);
            int pickY = (int) (request.getPickUp().getLongitude() + LON_OFFSET);

            int destX = (int) (request.getDestination().getLatitude() + LAT_OFFSET);
            int destY = (int) (request.getDestination().getLongitude() + LON_OFFSET);

            // c).i). Using DFS to reach at Pickup
            boolean pickUp = true;
            pathfindingService.simulateRide(sharedGrid, drvX, drvY, pickX, pickY,
                    closestDriver.getDriverId(), pickUp, request);
            sendMessagesToTopic.reachedAtPickup(request, closestDriver.getDriverId());

            // c).i). Using DFS to reach at Destination After Pickup
            pickUp = false;
            pathfindingService.simulateRide(sharedGrid, pickX, pickY, destX, destY,
                    closestDriver.getDriverId(), pickUp, request);
            sendMessagesToTopic.reachedAtDestination(request, closestDriver.getDriverId());

            Map<String, Object> result = new HashMap<>();
            result.put("driverId", closestDriver.getDriverId());
            result.put("status", "RIDE_COMPLETED");
            result.put("You Have to Pay: ", request.getEstimatedFare());
            return result;
        }
        request.setRideStatus(RideStatus.CANCELLED);
        sendMessagesToTopic.sendUpdatedRideStatus(request, closestDriver.getDriverId());
        throw new RuntimeException("Driver failed to accept ride");
    }


}
