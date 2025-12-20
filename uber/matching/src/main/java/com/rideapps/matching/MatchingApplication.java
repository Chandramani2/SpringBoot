package com.rideapps.matching;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoAuditing
@EnableMongoRepositories(basePackages = "com.rideapps.matching.Repository")
public class MatchingApplication {

	public static void main(String[] args) {
		SpringApplication.run(MatchingApplication.class, args);
	}

	// Define an in-memory grid (e.g., 20x20) initialized once at startup
	@Bean
	public int[][] sharedGrid() {
		// Latitude range (-90 to 90) + 90 offset = 180
		// Longitude range (-180 to 180) + 180 offset = 360
		return new int[181][361];
	}

}
