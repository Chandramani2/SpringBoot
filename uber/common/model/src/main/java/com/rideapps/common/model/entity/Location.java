package com.rideapps.common.model.entity;

//import jakarta.persistence.Column;
//import jakarta.persistence.Embeddable;
//import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Location {
//    @NotNull(message = "Latitude is required")
//    @Column(nullable = false)
    private double latitude;

//    @NotNull(message = "Longitude is required")
//    @Column(nullable = false)
    private double longitude;
}
