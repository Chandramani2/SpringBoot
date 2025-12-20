package com.rideapps.common.model.dto.Request;

import com.rideapps.common.model.entity.Location;
import com.rideapps.common.model.enums.PaymentMethod;
import com.rideapps.common.model.enums.RideStatus;
import com.rideapps.common.model.enums.RideTier;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RideParam {
    private Long rideId;
    private Long riderId;
    private Location pickUp;
    private Location destination;
    private RideTier tier;
    private PaymentMethod paymentMethod;
    private RideStatus rideStatus;
    private Long driverId;
    private double estimatedFare;
    private double surgeMultiplier;
    private LocalDateTime createdAt;
}