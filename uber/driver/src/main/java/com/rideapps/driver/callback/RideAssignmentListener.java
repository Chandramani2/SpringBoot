package com.rideapps.driver.callback;

import com.rideapps.common.model.dto.Request.AcceptRideRequest;

public interface RideAssignmentListener {
    void onRideAssigned(AcceptRideRequest request);
}