package com.rideapps.rider.controller;

import com.rideapps.common.model.dto.ApiResponse;
import com.rideapps.common.model.dto.Request.CreateRideRequest;
import com.rideapps.common.model.dto.Response.RideStatusResponse;
import com.rideapps.rider.model.Ride;
import com.rideapps.rider.service.RideMatchingService;
import com.rideapps.rider.service.RideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/v1/rides")
public class RideController {


    @Autowired
    private RideService rideService;

    @Autowired
    private RideMatchingService rideMatchingService;

    @PostMapping
    public ResponseEntity<ApiResponse<Ride>> createRide(@RequestBody CreateRideRequest request) {

        try {
            // Attempt to create and map the ride via the service
            Ride ride = rideService.createRide(request);
            // Return success response with the created ride object
            return ResponseEntity.ok(new ApiResponse<>(true, "Ride created successfully", ride));

        } catch (Exception e) {
            // Catch exceptions (mapping errors, database issues, etc.) and return the message
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Error creating ride: " + e.getMessage(), null));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RideStatusResponse>> getRideStatus(@PathVariable Long id){
        try{
            RideStatusResponse rideStatus = rideService.getRideStatus(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Successfully Fetched RideStatus: ", rideStatus));
        } catch (Exception e) {
            // Catch exceptions (mapping errors, database issues, etc.) and return the message
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Error in RideStatus: " + e.getMessage(), null));
        }

    }

    @PostMapping("/{id}/initiate")
    public ResponseEntity<ApiResponse<Map<String, Object>>> initiateRide(@PathVariable Long id) {
        try {
            // Attempt to create and map the ride via the service
            Ride ride = rideService.findRideById(id);
            // The service now handles exceptions internally and returns a Map
            Map<String, Object> matchingResult = rideMatchingService.initiateMatching(ride);

            // If the map contains the "error" key, use that string as the message
//        String message = matchingResult.containsKey("error")
//                ? (String) matchingResult.get("error")
//                : "Success";

            return ResponseEntity.ok(new ApiResponse<>(true, "Ride Completed Successfully:", matchingResult));
        }catch (Exception e) {
                // Catch exceptions (mapping errors, database issues, etc.) and return the message
                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ApiResponse<>(false, "Error Initiating ride: " + e.getMessage(), null));
        }
    }

}