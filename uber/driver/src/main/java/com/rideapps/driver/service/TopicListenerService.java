package com.rideapps.driver.service;

import com.rideapps.common.model.dto.Request.AcceptRideRequest;
import com.rideapps.common.model.enums.Status;
import com.rideapps.driver.Repository.DriverRepository;
import com.rideapps.driver.callback.RideAssignmentListener;
import com.rideapps.driver.model.Driver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TopicListenerService implements RideAssignmentListener {

    @Autowired
    private DriverRepository driverRepository;

    @Override
    public void onRideAssigned(AcceptRideRequest rideRequest) {
        System.out.println("Accepting ride: " + rideRequest.getDriverId());
        Driver driver = driverRepository.findById(rideRequest.getDriverId()).orElse(null);
        if(driver != null) {
            driver.setStatus(Status.NOT_AVAILABLE);

        }
    }

}
