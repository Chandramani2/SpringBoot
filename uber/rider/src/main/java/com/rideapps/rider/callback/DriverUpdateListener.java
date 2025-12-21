package com.rideapps.rider.callback;

import com.rideapps.common.model.dto.Request.AcceptRideRequest;

import java.util.Map;

public interface DriverUpdateListener {

    void updateRideStatus(Map<String, Object> payload);

    void driverLocationUpdate(Map<String, Object> payload);

    void getDriverLocation(Map<String, Object> payload);
}