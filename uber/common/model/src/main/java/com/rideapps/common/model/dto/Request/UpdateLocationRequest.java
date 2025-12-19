package com.rideapps.common.model.dto.Request;

import lombok.Data;

@Data
public class UpdateLocationRequest {
    private double latitude;
    private double longitude;
    private boolean available;
}
