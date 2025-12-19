package com.rideapps.rider.controller;

import com.rideapps.common.model.dto.Request.CreateRideRequest;
import com.rideapps.rider.model.Ride;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/rides")
public class RideController {

    @PostMapping
    public Ride createRide(@RequestBody CreateRideRequest createRideRequest){
        return null;
    }

}