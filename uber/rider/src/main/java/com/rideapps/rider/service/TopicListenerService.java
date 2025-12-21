package com.rideapps.rider.service;

import com.rideapps.common.model.dto.Request.AcceptRideRequest;
import com.rideapps.common.model.dto.Request.RideParam;
import com.rideapps.common.model.enums.Status;
import com.rideapps.driver.Repository.DriverRepository;
import com.rideapps.driver.callback.RideAssignmentListener;
import com.rideapps.driver.model.Driver;
import com.rideapps.rider.callback.DriverUpdateListener;
import com.rideapps.rider.repository.RideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

@Service
public class TopicListenerService implements DriverUpdateListener {

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RideService rideService;


    @Override
    public void onRideAssigned(AcceptRideRequest rideRequest) {
        System.out.println("Accepting ride: " + rideRequest.getDriverId());
        Driver driver = driverRepository.findById(rideRequest.getDriverId()).orElse(null);
        if(driver != null) {
            driver.setStatus(Status.NOT_AVAILABLE);

        }
    }

}
