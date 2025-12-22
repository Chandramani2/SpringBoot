# Matching Service Documentation

## Overview
The **Matching Service** is a critical engine within the ride-sharing platform responsible for connecting riders with the most suitable drivers. It utilizes spatial data, pathfinding algorithms, and real-time state management to calculate optimal routes and identify available drivers in proximity to a request.

---

## Technical Stack
* **Java**: Version 25.
* **Framework**: Spring Boot 4.0.0.
* **Caching/Persistence**:
  * **In-Memory Stores**: Uses specialized cache stores (e.g., `DriverLocationStore`, `RideStore`, `TripStore`) for high-speed access to volatile data.
  * **Database**: PostgreSQL for persistent location entry storage.
* **Algorithms**: Implements multiple pathfinding strategies including **A*** and **DFS**.
* **Communication**: REST for synchronous service requests and WebSockets for real-time location streaming.

---

## Core Logic & Strategies
### Pathfinding Strategies
The service uses a Strategy Pattern to calculate routes between coordinates:
* **A* (A-Star)**: An optimized heuristic-based search for finding the shortest path efficiently.
* **DFS (Depth-First Search)**: A recursive approach used for path exploration.
* **PathfindingActionRunner**: Dynamically executes the selected strategy to return navigation paths.

### Driver Matching
* **ClosestDriverService**: Calculates the Euclidean distance between a rider's pickup point and available drivers to find the best match.

---

## API Endpoints

### Matching Operations (`/v1/matching`)
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| **POST** | `/v1/matching/find-driver` | Receives rider coordinates and returns the best-matched driver and path.
| **GET** | `/v1/matching/path` | Calculates a path between two points using the active strategy.

### Location Management
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| **POST** | `/v1/matching/location/update` | Updates a driver's current position in the in-memory store.
| **GET** | `/v1/matching/location/{driverId}` | Retrieves the last known location of a specific driver.

---

## WebSocket & Real-time Communication
The service handles live location streams via STOMP WebSockets.

### Configuration
* **Endpoint**: `/ws-matching` (The endpoint where drivers connect).
* **Application Prefix**: `/app` (Prefix for messages bound for methods annotated with `@MessageMapping`).
* **Broker Prefix**: `/topic` (Enables a simple memory-based message broker to carry messages back to the client).

### Incoming Messages (Driver to Server)
Mapped in `MatchingSocketController.java`:
* **Topic**: `/app/driver.updateLocation`
  * **Payload**: `UpdateLocationRequest` (driverId, latitude, longitude, status).
  * **Function**: Saves or updates the driver's current position and status in the repository.
* **Topic**: `/app/update-status`
  * **Function**: Handles changes to driver status.

### Inbound (Messages to Matching)
* **Destination**: `/app/update-location`
* **Function**: Accepts frequent coordinate updates from drivers to keep the matching cache fresh.

### Outbound (Topics)
* **`/topic/driver-locations`**: Broadcasts the live positions of all active drivers to subscribed services (like the Rider Service for "nearby drivers" visualization).

---

## Configuration & Cache
* **Store Config**: Managed via `StoreConfig`, which initializes thread-safe maps for rides, trips, and driver locations.
* **Service Communication**: Uses `DriverRestClient` and `RiderRestClient` to notify other services when a match is successfully made or a status changes.
* **Server Port**: Configured in `application.properties` (typically port 8082).