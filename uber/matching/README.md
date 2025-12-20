# Matching Service - README

## Overview
The **Matching Service** is a core component of the ride-sharing platform responsible for connecting riders with available drivers. It utilizes real-time location tracking via WebSockets, calculates efficient paths using a custom pathfinding algorithm, and manages ride assignments based on proximity.

## Core Business Logic
The service revolves around three primary responsibilities:

* **Driver Location Management**: Drivers stream their GPS coordinates and availability status to the service via WebSockets. The service maintains the latest state for every driver in the system, either updating existing records or creating new entries in the repository.
* **Matching Algorithm**: When a ride request is received, the service filters for drivers currently marked as `AVAILABLE`. It then uses a **Manhattan Distance** calculation to find the driver closest to the rider's pickup point.
* **Pathfinding**: Once a match is made, the service generates two specific routes:
    * **Leg 1**: From the driver's current location to the pickup point.
    * **Leg 2**: From the pickup point to the final destination.
    * The pathfinding uses a **Depth-First Search (DFS)** algorithm. It is optimized by a heuristic that sorts directions based on their proximity to the target to simulate intelligent navigation.

---

## WebSocket Communication
The service uses STOMP over WebSockets to facilitate low-latency, bi-directional communication with driver applications.

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

### Outgoing Messages (Server to Driver)
Managed via `SimpMessagingTemplate` in `ClosestDriverService.java`:
* **Topic**: `/topic/ride-assignments`
    * **Function**: When a driver is matched, the service pushes ride details and a "New Ride Available!" notification to the driver.

---

## REST API Endpoints

### Find Driver and Calculate Path
* **URL**: `/v1/matching/find-driver`
* **Method**: `POST`
* **Body**: `RideParam` (Contains pickup and destination coordinates)
* **Workflow**:
    1.  Fetches drivers with the status `AVAILABLE`.
    2.  Identifies the closest driver using Manhattan distance.
    3.  Triggers a WebSocket notification for the driver to accept the ride.
    4.  Normalizes geographic coordinates to positive grid indices using predefined offsets (LAT: 90, LON: 180).
    5.  Calculates and returns the driver ID and two full coordinate paths (to pickup and to destination).

---

## Technical Stack
* **Java 25**
* **Spring Boot 4.0.1**
* **Spring WebSocket & Messaging**
* **Spring Data MongoDB** (For persisting driver locations)
* **Lombok** (For boilerplate reduction)

## Grid & Coordinate System
To facilitate pathfinding, the service uses a `sharedGrid`. The pathfinding algorithm treats grid cells with a value of `1` as obstacles and avoids previously visited cells during its search.