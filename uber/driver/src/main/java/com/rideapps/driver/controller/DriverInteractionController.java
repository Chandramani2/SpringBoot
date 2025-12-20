package com.rideapps.driver.controller;


import com.rideapps.common.model.entity.Location;
import com.rideapps.driver.model.Driver;
import com.rideapps.driver.service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/drivers")
public class DriverInteractionController {
    @Autowired
    private DriverService driverService;

    @PostMapping("/{id}/location")
    public void updateDriverLocation(@PathVariable Long id, @RequestBody Driver driverDetails){
            driverService.updateLocation(id,driverDetails);
    }
}
