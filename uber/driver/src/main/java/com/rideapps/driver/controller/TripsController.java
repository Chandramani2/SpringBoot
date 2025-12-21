package com.rideapps.driver.controller;

import com.rideapps.common.model.dto.ApiResponse;
import com.rideapps.common.model.dto.Request.CreateRideRequest;
import com.rideapps.common.model.dto.Request.RideParam;
import com.rideapps.driver.model.Trip;
import com.rideapps.driver.service.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/trips")
public class TripsController {

    @Autowired
    private TripService tripService;

    @PostMapping
    public ResponseEntity<ApiResponse<Trip>> createTrip(@RequestBody RideParam request) {

        try {
            // Attempt to create and map the ride via the service
            Trip trip = tripService.createTrip(request);
            // Return success response with the created ride object
            return ResponseEntity.ok(new ApiResponse<>(true, "Trip created successfully", trip));

        } catch (Exception e) {
            // Catch exceptions (mapping errors, database issues, etc.) and return the message
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "[Trip]: Error creating ride: " + e.getMessage(), null));
        }
    }
}
