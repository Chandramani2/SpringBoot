package com.rideapps.rider.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.rideapps.common.model.entity.Location;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.rideapps.common.model.enums.RideStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Trip {
    private String tripId;
    private String rideId;
    private String driverId;
    private String riderId;
    private Location startLocation;
    private Location endLocation;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startTime;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endTime;
    private double distance;
    private long durationMinutes;
    private double finalFare;
    private RideStatus status;
}