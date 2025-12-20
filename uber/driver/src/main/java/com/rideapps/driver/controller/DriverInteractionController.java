package com.rideapps.driver.controller;


import com.rideapps.common.model.dto.ApiResponse;
import com.rideapps.common.model.dto.Request.AcceptRideRequest;
import com.rideapps.common.model.entity.Location;
import com.rideapps.driver.model.Driver;
import com.rideapps.driver.service.DriverService;
import com.rideapps.driver.service.TopicListenerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/drivers")
public class DriverInteractionController {
    @Autowired
    private DriverService driverService;

    @Autowired
    private TopicListenerService topicListenerService;

    @PostMapping("/{id}/location")
    public ResponseEntity<ApiResponse<Driver>> updateDriverLocation(@PathVariable Long id, @RequestBody Driver driverDetails){
        Driver driver = driverService.updateLocation(id,driverDetails);
        return ResponseEntity.ok(new ApiResponse<>(true, "Driver location updated successfully", driver));

    }

    @PostMapping("/{id}/accept")
    public synchronized ResponseEntity<ApiResponse<Boolean>> acceptRide(@RequestBody AcceptRideRequest rideRequest, @PathVariable Long id){
        try {
            rideRequest.setDriverId(id);
            topicListenerService.onRideAssigned(rideRequest);
            return ResponseEntity.ok(new ApiResponse<>(true, "Driver Accepted Ride: ", true));
        } catch (Exception e) {
            // Catch exceptions (mapping errors, database issues, etc.) and return the message
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "[Driver] Error In Accepting Ride: " + e.getMessage(), false));
        }
    }

}
