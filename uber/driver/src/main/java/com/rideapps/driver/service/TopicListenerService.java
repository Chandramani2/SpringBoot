package com.rideapps.driver.service;

import com.rideapps.common.model.dto.Request.AcceptRideRequest;
import com.rideapps.common.model.dto.Request.RideParam;
import com.rideapps.common.model.enums.Status;
import com.rideapps.driver.Repository.DriverRepository;
import com.rideapps.driver.callback.RideAssignmentListener;
import com.rideapps.driver.model.Driver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

@Service
public class TopicListenerService implements RideAssignmentListener {

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TripService tripService;

    @Autowired
    private SendMessagesToTopic sendMessagesToTopic;

    @Override
    public void onRideAssigned(AcceptRideRequest rideRequest) {
        System.out.println("Accepting ride: " + rideRequest.getDriverId());
        Driver driver = driverRepository.findById(rideRequest.getDriverId()).orElse(null);
        if(driver != null) {
            driver.setStatus(Status.NOT_AVAILABLE);
            driverRepository.save(driver);
        }
    }

    @Override
    public void getRideAssignmentTopic(Map<String, Object> payload) {
        if(payload.containsKey("acceptRide")) {
            AcceptRideRequest rideRequest = objectMapper.convertValue(payload, AcceptRideRequest.class);
            onRideAssigned(rideRequest);
        }
        if(payload.containsKey("rideCompleted")) {
            RideParam rideParam = objectMapper.convertValue(payload, RideParam.class);
            tripService.createTrip(rideParam);
            sendMessagesToTopic.sendRideStatusToRider(payload);
        }
    }

    @Override
    public void getDriverUpdateTopic(Map<String, Object> payload) {
        if(payload.containsKey("driverLocation")) {
            sendMessagesToTopic.sendDriverLocationToRider(payload);
        }
        if(payload.containsKey("driverStatus")) {
            sendMessagesToTopic.sendRideStatusToRider(payload);
        }
    }

    @Override
    public void getDriverLocation(Map<String, Object> payload) {
        if(payload.containsKey("reachedPickup")) {
            sendMessagesToTopic.driverReachedLocation(payload);
        }
        if(payload.containsKey("reachedDestination")) {
            sendMessagesToTopic.driverReachedLocation(payload);
        }
    }

}
