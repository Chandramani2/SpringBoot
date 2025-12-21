package com.rideapps.matching.cache;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StoreConfig {

    @Bean
    public DriverLocationStore driverLocationStore() {
        return new DriverLocationStore();
    }

    @Bean
    public RideStore rideStore() {
        return new RideStore();
    }

    @Bean
    public TripStore tripStore() {
        return new TripStore();
    }

    @Bean
    public PaymentStore paymentStore() {
        return new PaymentStore();
    }
}
