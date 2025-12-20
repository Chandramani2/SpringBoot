package com.rideapps.common.model.dto.Response;

import com.rideapps.common.model.entity.Location;
import com.rideapps.common.model.enums.PaymentMethod;
import com.rideapps.common.model.enums.RideStatus;
import com.rideapps.common.model.enums.RideTier;
import lombok.Data;

@Data
public class RideStatusResponse {
    private RideStatus rideStatus;
    private Long riderId;
    private RideTier tier;
    private PaymentMethod paymentMethod;
    private Location pickUp;
    private Location destination;

}
