package com.rideapps.rider.service;

import com.rideapps.common.model.dto.Request.CreateRideRequest;
import com.rideapps.common.model.dto.Response.RideStatusResponse;
import com.rideapps.common.model.enums.RideStatus;
import com.rideapps.rider.model.Ride;

import com.rideapps.rider.repository.RideRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RideService {

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private ObjectMapper objectMapper;

    public List<Ride> getAllRides(){
        return rideRepository.findAll();
    }

    public Ride findRideById(Long id){
        return rideRepository.findById(id).orElse(null);
    }

    public void registerRide(Ride ride){
        rideRepository.save(ride);
    }

    public void registerRideList(List<Ride> rideList){
        rideRepository.saveAll(rideList);
    }


    public Ride createRide(CreateRideRequest request) {
        try {
            // 1. Calculate Distance and Fare
            double distance = calculateDistance(
                    request.getPickupLatitude(), request.getPickupLongitude(),
                    request.getDestinationLatitude(), request.getDestinationLongitude()
            );

            double surgeMultiplier = 1.5; // Example logic: could be fetched from a dynamic service
            double baseFare = 50.0;
            double perKmRate = 12.0;
            double estimatedFare = (baseFare + (distance * perKmRate)) * surgeMultiplier;

            // 2. Map fields to a Dictionary (Map)
            Map<String, Object> rideData = new HashMap<>();
            rideData.put("riderId", request.getRiderId());
            rideData.put("tier", request.getTier());
            rideData.put("paymentMethod", request.getPaymentMethod());
            rideData.put("rideStatus", RideStatus.REQUESTED);

            // Map embedded objects
            Map<String, Object> pickUp = new HashMap<>();
            pickUp.put("latitude", request.getPickupLatitude());
            pickUp.put("longitude", request.getPickupLongitude());
            rideData.put("pickUp", pickUp);

            Map<String, Object> destination = new HashMap<>();
            destination.put("latitude", request.getDestinationLatitude());
            destination.put("longitude", request.getDestinationLongitude());
            rideData.put("destination", destination);

            // Set calculated fare fields
            rideData.put("estimatedFare", estimatedFare);
            rideData.put("surgeMultiplier", surgeMultiplier);
            rideData.put("driverId", 0L); // Placeholder until matched

            // 3. Convert Map to Ride Entity using ObjectMapper
            Ride ride = objectMapper.convertValue(rideData, Ride.class);

            return rideRepository.save(ride);
        } catch (Exception e) {
            throw new RuntimeException("Mapping or Fare Calculation failed: " + e.getMessage());
        }
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // Simple Haversine or Euclidean distance formula
        return Math.sqrt(Math.pow(lat2 - lat1, 2) + Math.pow(lon2 - lon1, 2)) * 111.0;
    }

    public RideStatusResponse getRideStatus(Long id) {
        try{
            Ride ride = rideRepository.findById(id).orElse(null);
            return objectMapper.convertValue(ride, RideStatusResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("Ride Not Found: " + e.getMessage());
        }

    }
}
