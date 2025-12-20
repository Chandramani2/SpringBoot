package com.rideapps.matching.service;

import com.rideapps.matching.Repository.DriverLocationRepository;
import com.rideapps.matching.dto.Request.UpdateLocationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DriverLocationEntryService {

    @Autowired
    private DriverLocationRepository driverLocationRepository;


    public void saveEntry(UpdateLocationRequest updateLocation) {
        // 1. Check if a location entry already exists for this driverId
        UpdateLocationRequest existingEntry = driverLocationRepository.findByDriverId(updateLocation.getDriverId());

        if (existingEntry != null) {
            // 2. Update the existing record with new data
            existingEntry.setLatitude(updateLocation.getLatitude());
            existingEntry.setLongitude(updateLocation.getLongitude());
            existingEntry.setStatus(updateLocation.getStatus());
            // createdAt is READ_ONLY, but LastModifiedDate will update automatically
            driverLocationRepository.save(existingEntry);
        } else {
            // 3. Create a new record if it doesn't exist
            driverLocationRepository.save(updateLocation);
        }
    }

}
