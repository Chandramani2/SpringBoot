package com.rideapps.rider.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.rideapps.common.model.entity.Location;
import com.rideapps.common.model.enums.PaymentMethod;
import com.rideapps.common.model.enums.RideStatus;
import com.rideapps.common.model.enums.RideTier;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Ride {
    private String rideId;
    private String riderId;
    private Location pickup;
    private Location destination;
    private RideTier tier;
    private PaymentMethod paymentMethod;
    private RideStatus status;
    private Long driverId;
    private double estimatedFare;
    private double surgeMultiplier;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

}
