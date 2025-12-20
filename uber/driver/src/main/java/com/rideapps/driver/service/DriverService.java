package com.rideapps.driver.service;

import com.rideapps.common.model.entity.Location;
import com.rideapps.driver.Repository.DriverRepository;
import com.rideapps.driver.controller.DriverSocketController;
import com.rideapps.driver.model.Driver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DriverService {
    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private DriverMatchingClientService driverMatchingClientService;

    public List<Driver> getAllDriver(){
        return driverRepository.findAll();
    }

    public Driver getDriverById(Long id){
        return driverRepository.findById(id).orElse(null);
    }

    public void registerDriver(Driver driver){
        driverRepository.save(driver);
    }

    public void registerDriverList(List<Driver> driverList){
        driverRepository.saveAll(driverList);
    }


    public Driver updateLocation(Long driverId, Driver driverDetails) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        driver.setDriverLocation(driverDetails.getDriverLocation());

        // Logic: If driver reached the target coordinates (logic from your pathfinding)
        // you can set them back to AVAILABLE here
        driver.setStatus(driverDetails.getStatus());
        Driver updatedDriver = driverRepository.save(driver);

        // Sync with Matching Service so it knows the driver is free/at new location
        Map<String, Object> payload = new HashMap<>();
        payload.put("driverId", updatedDriver.getDriverId());
        payload.put("latitude", updatedDriver.getDriverLocation().getLatitude());
        payload.put("longitude", updatedDriver.getDriverLocation().getLongitude());
        payload.put("status", updatedDriver.getStatus());

        driverMatchingClientService.sendLocationUpdate(payload);

        return updatedDriver;
    }
}
