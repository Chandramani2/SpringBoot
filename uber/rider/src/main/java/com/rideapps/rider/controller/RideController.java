package com.rideapps.rider.controller;

import com.rideapps.common.model.dto.ApiResponse;
import com.rideapps.common.model.dto.Request.CreateRideRequest;
import com.rideapps.rider.service.CreateRideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/rides")
public class RideController {

    @Autowired
    private CreateRideService createRideService;

    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> createRide(@RequestBody CreateRideRequest request) {
        // The service now handles exceptions internally and returns a Map
        Map<String, Object> matchingResult = createRideService.initiateMatching(request);

        // If the map contains the "error" key, use that string as the message
        String message = matchingResult.containsKey("error")
                ? (String) matchingResult.get("error")
                : "Success";

        return ResponseEntity.ok(new ApiResponse<>(true, message, matchingResult));
    }

}