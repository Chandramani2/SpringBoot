package com.rideapps.driver.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rideapps.common.model.constants.StatusProperty;
import com.rideapps.common.model.entity.Location;
import com.rideapps.common.model.enums.RideStatus;
import com.rideapps.common.model.enums.RideTier;

import com.rideapps.common.model.enums.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "drivers")
@EntityListeners(AuditingEntityListener.class) //Required to Auto Fill createdAt, updatedAt
public class Driver {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long driverId;

    @NotBlank(message = "Driver Name is required")
    @Column(nullable = false)
    private String driverName;

    @NotBlank(message = "Phone number is required")
    @Column(nullable = false, unique = true)
    private String phoneNumber;

    @NotNull(message = "Location is required")
    @Column(nullable = false)
    @Embedded
    private Location driverLocation;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(255)")
    private Status status;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RideTier tier = RideTier.STANDARD;


    @CreatedDate // Automatically sets date on creation
    @Column(updatable = false, nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY) // Hide from request body
    private LocalDateTime lastLocationUpdate;
}
