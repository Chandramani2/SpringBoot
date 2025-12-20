# Driver Service - README

## Overview
The **Driver Service** acts as the client-side interface for drivers within the ride-sharing ecosystem. It manages driver profiles, tracks their real-time state, and maintains a persistent WebSocket connection to the Matching Service to receive and respond to ride assignments.

## Core Business Logic
The service manages the lifecycle and interactions of a driver through several key processes:

* **Driver Lifecycle Management**: The service handles the creation and retrieval of driver profiles, including their current status (e.g., `AVAILABLE`, `ON_TRIP`) and personal details.
* **Real-Time Connectivity**: Upon startup, the service establishes a dedicated STOMP WebSocket session with the Matching Service. This connection is used to:
    * Stream periodic location updates to the matching engine.
    * Listen for incoming ride requests specifically assigned to the driver.
* **Ride Assignment Handling**: When a "New Ride Available" message is received via the WebSocket, the service processes the payload and notifies the driver. The driver can then interact with the system to accept or manage the trip.
* **Trip Management**: The service tracks the history and current state of trips assigned to a driver, allowing for status updates as the driver progresses from pickup to destination.

---

## WebSocket Client Communication
Unlike the Matching Service which acts as a broker, the Driver Service acts as a **WebSocket Client**.

### Configuration
* **Connection URL**: Connects to the Matching Service at `/ws-matching`.
* **Session Handling**: Managed by `DriverStompSessionHandler`, which handles connection established events and subscription logic.

### Outbound Communication (Driver to Matching Service)
* **Topic**: `/app/driver.updateLocation`
    * **Function**: Sends regular updates containing the driver's ID, current coordinates, and availability status to the Matching Service.
* **Topic**: `/app/update-status`
    * **Function**: Notifies the matching engine of manual status changes.

### Inbound Communication (Matching Service to Driver)
* **Subscription**: `/topic/ride-assignments`
    * **Callback**: `RideAssignmentListener`.
    * **Function**: Listens for payloads containing new ride details. When a match is made by the Matching Service, this listener captures the assignment and triggers the local business logic to alert the driver.

---

## REST API Endpoints

### Driver Management
* **URL**: `/v1/driver/{id}`
* **Method**: `GET`
* **Function**: Retrieves specific driver details and current status.

### Trip Interactions
* **URL**: `/v1/driver/trips/{driverId}`
* **Method**: `GET`
* **Function**: Fetches the list of trips (history and active) associated with a specific driver.

---

## Technical Stack
* **Java 25**
* **Spring Boot 4.0.1**
* **Standard WebSocket Client**: Uses `StandardWebSocketClient` and `WebSocketStompClient` for external service communication.
* **Spring Data MongoDB**: For persisting driver profiles and trip records.
* **Lombok**: Used for model and DTO boilerplate.

## Key Components
* **DriverMatchingClientService**: Logic for initiating the WebSocket connection and sending updates to the Matching Service.
* **TopicListenerService**: Manages the subscriptions to various topics once the WebSocket session is active.
* **DriverStompSessionHandler**: Implements the STOMP session lifecycle, including handling transport errors and frame conversion.