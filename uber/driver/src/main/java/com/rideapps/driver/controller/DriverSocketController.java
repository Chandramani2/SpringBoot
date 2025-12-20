package com.rideapps.driver.controller;

import com.rideapps.driver.model.Driver;
import com.rideapps.driver.service.DriverMatchingClientService;
import com.rideapps.driver.service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
public class DriverSocketController {

    @Autowired
    private DriverMatchingClientService driverMatchingClientService;
    @Autowired
    private DriverService driverService;


    @MessageMapping("/driver.updateLocation")
    @SendTo("/topic/update-driver-location")
    public void sendMessage(@Payload Map<String, Object> payload ) {
        System.out.println("DriverSocketController.sendMessage: " + payload);
        driverMatchingClientService.sendLocationUpdate(payload);
    }

}
