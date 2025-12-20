package com.rideapps.rider.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rideapps.common.model.entity.Location;
import com.rideapps.common.model.enums.PaymentMethod;
import com.rideapps.common.model.enums.RideStatus;
import com.rideapps.common.model.enums.RideTier;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "rides")
@EntityListeners(AuditingEntityListener.class)
public class Ride {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rideId;

    @NotNull(message = "Rider Id is required")
    @Column(nullable = false)
    private Long riderId;

    @NotNull(message = "Location is required")
    @Column(nullable = false)
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "latitude", column = @Column(name = "pickup_latitude")),
            @AttributeOverride(name = "longitude", column = @Column(name = "pickup_longitude"))
    })
    private Location pickUp;

    @NotNull(message = "Location is required")
    @Column(nullable = false)
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "latitude", column = @Column(name = "destination_latitude")),
            @AttributeOverride(name = "longitude", column = @Column(name = "destination_longitude"))
    })
    private Location destination;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RideTier tier = RideTier.STANDARD;

    @NotNull(message = "paymentMethod is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(255)")
    private PaymentMethod paymentMethod;

    @NotNull(message = "rideStatus is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(255)")
    private RideStatus rideStatus;

    @NotNull(message = "Driver Id is required")
    @Column(nullable = false)
    private Long driverId;

    @NotNull(message = "estimatedFare is required")
    @Column(nullable = false)
    private double estimatedFare;

    @Column(nullable = false)
    private double surgeMultiplier = 1.5;

    @CreatedDate // Automatically sets date on creation
    @Column(updatable = true, nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdAt;
}
