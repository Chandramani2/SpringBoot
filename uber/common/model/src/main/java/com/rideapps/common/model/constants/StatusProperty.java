package com.rideapps.common.model.constants;

public interface StatusProperty {

    interface USER_STATUS {
        String FREE = "free";
        String BOOKED = "booked";
    }

    interface DRIVER_STATUS {
        String AVAILABLE = "available";
        String ACCEPTED_RIDE = "accepted_ride";
        String USER_ONBOARD = "user_onboard";
        String USER_DROPPED = "user_dropped";
    }

    interface TRIPS_STATUS {
        String ONGOING = "ongoing";
        String COMPLETED = "completed";
    }
}
