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

    public Driver updateLocation(Long driverId, Driver driverDetails){

        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        driver.setDriverLocation(driverDetails.getDriverLocation());
        driver.setStatus(driverDetails.getStatus());
        Driver updatedDriver = driverRepository.save(driver);

        // Convert Driver POJO to a Map or a DTO that matches UpdateLocationRequest
        // This avoids the "Unable to convert payload" error
        Map<String, Object> locationUpdatePayload = new HashMap<>();
        locationUpdatePayload.put("driverId", updatedDriver.getDriverId());
        locationUpdatePayload.put("latitude", updatedDriver.getDriverLocation().getLatitude());
        locationUpdatePayload.put("longitude", updatedDriver.getDriverLocation().getLongitude());
        locationUpdatePayload.put("status", updatedDriver.getStatus());
        // Add other fields required by UpdateLocationRequest if necessary

        driverMatchingClientService.sendLocationUpdate(locationUpdatePayload);

        return updatedDriver;
    }
}
