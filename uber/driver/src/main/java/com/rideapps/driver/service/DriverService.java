package com.rideapps.driver.service;

import com.rideapps.driver.Repository.DriverRepository;
import com.rideapps.driver.model.Driver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DriverService {
    @Autowired
    private DriverRepository driverRepository;

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
}
