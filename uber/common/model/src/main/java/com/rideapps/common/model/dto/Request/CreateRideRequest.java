package com.rideapps.common.model.dto.Request;

import com.rideapps.common.model.enums.PaymentMethod;
import com.rideapps.common.model.enums.RideTier;
import lombok.Data;

@Data
public class CreateRideRequest {
    private Long riderId;
    private double pickupLatitude;
    private double pickupLongitude;
    private double destinationLatitude;
    private double destinationLongitude;
    private RideTier tier;
    private PaymentMethod paymentMethod;
}
