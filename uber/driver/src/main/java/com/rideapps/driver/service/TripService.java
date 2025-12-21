package com.rideapps.driver.service;

import com.rideapps.common.model.dto.Request.CreateRideRequest;
import com.rideapps.common.model.dto.Request.RideParam;
import com.rideapps.common.model.enums.RideStatus;
import com.rideapps.driver.Repository.TripRepository;
import com.rideapps.driver.model.Trip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class TripService {

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SendMessagesToTopic sendMessagesToTopic;

    public Trip createTrip(RideParam request) {
        try {
            // 1. Calculate Distance and Fare
            double distance = calculateDistance(
                    request.getPickUp().getLatitude(), request.getPickUp().getLongitude(),
                    request.getDestination().getLatitude(), request.getDestination().getLongitude()
            );

            double finalFare = request.getEstimatedFare();

            // 2. Map fields to a Dictionary (Map)
            Map<String, Object> tripData = new HashMap<>();
            tripData.put("rideId", request.getRideId());
            tripData.put("driverId", request.getDriverId());
            tripData.put("riderId", request.getRiderId());
            tripData.put("distance", distance);
            tripData.put("durationMinutes", 5);
            tripData.put("paymentMethod", request.getPaymentMethod());
            tripData.put("rideStatus", RideStatus.COMPLETED);

            // Map embedded objects
            Map<String, Object> pickUp = new HashMap<>();
            pickUp.put("latitude", request.getPickUp().getLatitude());
            pickUp.put("longitude", request.getPickUp().getLongitude());
            tripData.put("pickUp", pickUp);

            Map<String, Object> destination = new HashMap<>();
            destination.put("latitude", request.getDestination().getLatitude());
            destination.put("longitude", request.getDestination().getLongitude());
            tripData.put("destination", destination);

            // Set calculated fare fields
            tripData.put("finalFare", finalFare);

            //set End Time
            tripData.put("endTime", LocalDateTime.now().withNano(0));

            // 3. Convert Map to Ride Entity using ObjectMapper
            Trip trip = objectMapper.convertValue(tripData, Trip.class);

            return tripRepository.save(trip);
        } catch (Exception e) {
            throw new RuntimeException("[Trip]: Mapping or Fare Calculation failed: " + e.getMessage());
        }
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // Simple Haversine or Euclidean distance formula
        return Math.sqrt(Math.pow(lat2 - lat1, 2) + Math.pow(lon2 - lon1, 2)) * 111.0;
    }
}
