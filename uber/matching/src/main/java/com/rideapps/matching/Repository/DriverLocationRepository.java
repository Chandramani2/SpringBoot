package com.rideapps.matching.Repository;


import com.rideapps.common.model.enums.Status;
import com.rideapps.matching.dto.Request.UpdateLocationRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DriverLocationRepository extends MongoRepository<UpdateLocationRequest, String> {
    UpdateLocationRequest findByDriverId(Long driverId);
    // New method to fetch only available drivers
    List<UpdateLocationRequest> findByStatus(Status status);
}
