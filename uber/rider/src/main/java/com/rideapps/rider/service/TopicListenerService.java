package com.rideapps.rider.service;

import com.rideapps.common.model.dto.Request.RideParam;
import com.rideapps.common.model.enums.RideStatus;
import com.rideapps.rider.callback.DriverUpdateListener;
import com.rideapps.rider.model.Ride;
import com.rideapps.rider.model.User;
import com.rideapps.rider.repository.RideRepository;
import com.rideapps.rider.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

@Service
public class TopicListenerService implements DriverUpdateListener {

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;


    @Override
    public void updateRideStatus(Map<String, Object> payload) {
        if (payload.containsKey("rideStatusChanged")) {
            RideParam rideParam = objectMapper.convertValue(payload, RideParam.class);
            System.out.println("Updating Ride Status: " + rideParam.getRideId());
            Ride ride = rideRepository.findById(rideParam.getRideId()).orElse(null);
            if(ride != null) {
                ride.setRideStatus(rideParam.getRideStatus());
                ride.setDriverId(rideParam.getDriverId());
                rideRepository.save(ride);
            }
        }

    }

    @Override
    public void driverLocationUpdate(Map<String, Object> payload) {
        System.out.println("Live Driver Location: " + payload.toString());
    }

    @Override
    public void getDriverLocation(Map<String, Object> payload) {
         RideParam rideParam = objectMapper.convertValue(payload, RideParam.class);
            System.out.println("Reached Detination: " + rideParam.getRideId());
            Ride ride = rideRepository.findById(rideParam.getRideId()).orElse(null);
            if(ride != null) {
                ride.setRideStatus(rideParam.getRideStatus());
                ride.setDriverId(rideParam.getDriverId());
                rideRepository.save(ride);
            }
            if(payload.containsKey("reachedDestination")) {
            User user = userRepository.findById(rideParam.getRiderId()).orElse(null);
            if(user != null) {
                user.setPaymentPending(rideParam.getEstimatedFare());
                userRepository.save(user);
            }
            System.out.println("We have reached Your Destination: " + payload.get("destination").toString());
        }
        if(payload.containsKey("reachedPickup")) {
            System.out.println("Driver Arrived at your pickup location: " + payload.get("pickUp").toString());
        }

    }
}
