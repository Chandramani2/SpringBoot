package com.rideapps.driver.controller;

import com.rideapps.common.model.dto.ApiResponse;
import com.rideapps.driver.model.Driver;
import com.rideapps.driver.service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/v1/driver")
public class DriverController {


    @Autowired
    private DriverService driverService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Driver>>> getAllDriver(){

        return ResponseEntity.ok(new ApiResponse<>(true, "User retrieved successfully", driverService.getAllDriver()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Driver>> getDriver(@PathVariable Long id) {
        Driver driver = driverService.getDriverById(id);

        if (driver == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(true, "User not found", null));
        }

        return ResponseEntity.ok(new ApiResponse<>(true, "Driver retrieved successfully", driver));
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<String>> addDriver(@RequestBody Driver driver){
        driverService.registerDriver(driver);
        return ResponseEntity.ok(new ApiResponse<>(true, "Driver Registered successfully", null));
    }

    @PostMapping("/createList")
    public ResponseEntity<ApiResponse<String>> addDriverList(@RequestBody List<Driver> driverList){
        driverService.registerDriverList(driverList);
        return ResponseEntity.ok(new ApiResponse<>(true, "All Driver Registered successfully", null));
    }

}
