# Rider Service - README

## Overview
The **Rider Service** is the primary interface for passengers within the ride-sharing platform. It manages user profiles, handles ride requests, coordinates with the Matching Service to find drivers, and processes payment transactions.

## Core Business Logic
The service manages the rider's journey through several key processes:

* **User Management**: Handles the creation and management of passenger accounts, storing essential profile data and preferences.
* **Ride Life-cycle**: Allows riders to create new ride requests by specifying pickup and destination coordinates.
* **Driver Discovery**: Coordinates with the **Matching Service** via a specialized client to find the closest available driver for a requested trip.
* **Trip Monitoring**: Once a ride is matched, the service tracks the status of the ride (e.g., `REQUESTED`, `ACCEPTED`, `COMPLETED`).
* **Payment Processing**: Manages the financial aspect of the ride, including calculating fares and recording transaction details.

---

## Service Communication

### Internal Communication (Rider to Matching Service)
The Rider Service acts as a REST client to the Matching Service:
* **Interaction**: When a rider requests a trip, the `RideMatchingService` calls the matching engine's REST API.
* **Payload**: Sends pickup and destination coordinates.
* **Response**: Receives the matched driver's details and the calculated navigation paths.

### Persistent Data Handling
The service ensures data consistency through:
* **Ride Tracking**: Every request is assigned a unique ID and saved in the repository to maintain a history of user trips.
* **User State**: Tracks whether a user is currently in a ride to prevent duplicate requests.

---

## REST API Endpoints

### Ride Management
* **URL**: `/v1/rider/request-ride`
* **Method**: `POST`
* **Function**: Initiates the matching process and creates a ride record in the database.
* **URL**: `/v1/rider/rides/{userId}`
* **Method**: `GET`
* **Function**: Retrieves all ride history for a specific passenger.

### User Management
* **URL**: `/v1/user/create`
* **Method**: `POST`
* **Function**: Registers a new passenger in the system.

### Payment
* **URL**: `/v1/payment/process`
* **Method**: `POST`
* **Function**: Records and processes payment for completed trips.

---

## Technical Stack
* **Java 25**
* **Spring Boot 4.0.1**
* **Spring Data MongoDB**: Used for persisting rider profiles, ride details, and payment records.
* **Lombok**: Utilized for reducing boilerplate code in models and DTOs.
* **RestTemplate**: Used for synchronous communication with the Matching Service.

## Key Components
* **RideMatchingService**: The bridge between the Rider Service and the Matching Service engine.
* **RideService**: Core logic for validating and managing ride states.
* **UserService**: Manages passenger account business rules.