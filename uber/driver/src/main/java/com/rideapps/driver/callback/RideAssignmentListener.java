package com.rideapps.driver.callback;

import com.rideapps.common.model.dto.Request.AcceptRideRequest;

import java.util.Map;

public interface RideAssignmentListener {
    void onRideAssigned(AcceptRideRequest request);

    void getRideAssignmentTopic(Map<String, Object> payload);

    void getDriverUpdateTopic(Map<String, Object> payload);

    void getDriverLocation(Map<String, Object> payload);
}