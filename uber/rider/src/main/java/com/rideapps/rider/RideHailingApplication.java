package com.rideapps.rider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class RideHailingApplication {
	public static void main(String[] args) {
		SpringApplication.run(RideHailingApplication.class, args);
	}

}
