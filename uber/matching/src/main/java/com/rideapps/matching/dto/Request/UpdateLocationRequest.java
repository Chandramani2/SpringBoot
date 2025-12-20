package com.rideapps.matching.dto.Request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rideapps.common.model.entity.Location;
import com.rideapps.common.model.enums.RideStatus;
import com.rideapps.common.model.enums.Status;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;


@Data
@Document(collection = "driver_locations")
public class UpdateLocationRequest {
    @Id
    private Long driverId;
    private double latitude;
    private double longitude;
    private Status status;
    private RideStatus rideStatus;

    @CreatedDate // Automatically sets date on creation
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY) // Hide from request body
    private LocalDateTime createdAt;

    @LastModifiedDate // Automatically updates date on modification
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY) // Hide from request body
    private LocalDateTime updatedAt;

}
