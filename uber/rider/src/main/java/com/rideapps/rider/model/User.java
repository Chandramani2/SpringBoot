package com.rideapps.rider.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.rideapps.common.model.entity.Location;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class) //Required to Auto Fill createdAt, updatedAt
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @NotBlank(message = "Username is required")
    @Column(nullable = false)
    private String userName;

    @NotBlank(message = "Phone number is required")
    @Column(nullable = false, unique = true)
    private String phoneNumber;

    @NotNull(message = "Location is required")
    @Column(nullable = false)
    @Embedded
    private Location userLocation;

    @CreatedDate // Automatically sets date on creation
    @Column(updatable = false, nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY) // Hide from request body
    private LocalDateTime createdAt;

    @LastModifiedDate // Automatically updates date on modification
    @Column(nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY) // Hide from request body
    private LocalDateTime updatedAt;

}
