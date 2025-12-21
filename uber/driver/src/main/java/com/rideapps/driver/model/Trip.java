package com.rideapps.driver.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rideapps.common.model.entity.Location;
import com.rideapps.common.model.enums.RideStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "trips")
@EntityListeners(AuditingEntityListener.class)
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tripId;

    @NotNull(message = "Ride Id is required")
    @Column(nullable = false, unique = true)
    private Long rideId;

    @NotNull(message = "Driver Id is required")
    @Column(nullable = false)
    private Long driverId;

    @NotNull(message = "Rider Id is required")
    @Column(nullable = false)
    private Long riderId;

    @NotNull(message = "Pickup location is required")
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "latitude", column = @Column(name = "pickup_latitude", nullable = false)),
            @AttributeOverride(name = "longitude", column = @Column(name = "pickup_longitude", nullable = false))
    })
    private Location pickUp;

    @NotNull(message = "Destination location is required")
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "latitude", column = @Column(name = "destination_latitude", nullable = false)),
            @AttributeOverride(name = "longitude", column = @Column(name = "destination_longitude", nullable = false))
    })
    private Location destination;

    @CreatedDate
    @Column(name = "start_time", updatable = false, nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime startTime;

    @Column(name = "end_time")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endTime;

    @NotNull(message = "Distance is required")
    @Column(nullable = false)
    private Double distance;

    @NotNull(message = "Duration in minutes is required")
    @Column(nullable = false)
    private Long durationMinutes;

    @NotNull(message = "Final fare is required")
    @Column(nullable = false)
    private Double finalFare;

    @NotNull(message = "rideStatus is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private RideStatus rideStatus;
}