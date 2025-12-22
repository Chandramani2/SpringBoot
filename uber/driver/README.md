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

## Technical Stack
* **Java**: Version 25.
* **Framework**: Spring Boot 4.0.0.
* **Database**: PostgreSQL (for persisting driver profiles and trip history).
* **Communication**:
  * **REST**: For synchronous interactions like profile updates and trip lookups.
  * **WebSockets (STOMP)**: For real-time updates, including ride assignments and location tracking.
* **Utilities**: Lombok for boilerplate reduction and Jackson for JSON processing.

---

## Core Models
* **Driver**: Represents a service provider, including fields for `driverName`, `phoneNumber`, `licensePlate`, `status` (e.g., AVAILABLE, BUSY), and current `location`.
* **Trip**: Records details of an ongoing or completed journey, including `riderId`, `driverId`, `source` and `destination` coordinates, `fare`, and `tripStatus`.

---

## API Endpoints

### Driver Management (`/v1/driver`)
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| **GET** | `/v1/driver` | Retrieves a list of all drivers. |
| **GET** | `/v1/driver/{id}` | Fetches details for a specific driver. |
| **POST** | `/v1/driver/register` | Registers a new driver in the system. |
| **POST** | `/v1/driver/status` | Updates a driver's availability status. |

### Trip Management (`/v1/trips`)
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| **GET** | `/v1/trips/{id}` | Retrieves specific trip details. |
| **POST** | `/v1/trips/update-status` | Updates the progress of a trip (e.g., STARTED, COMPLETED). |

### Interaction & Simulation (`/v1/interaction`)
* **Endpoint**: `/v1/interaction/simulate-location`
* **Function**: Used to simulate driver movement for testing and tracking purposes.

---

## WebSocket & Real-time Communication
The service acts as a WebSocket server to maintain persistent connections with driver applications.

### Inbound (Messages from Drivers)
* **Destination**: `/app/driver.updateLocation`
* **Function**: Receives real-time coordinate updates from drivers.
* **Destination**: `/app/driver.acceptRide`
* **Function**: Allows a driver to accept a pending ride request.

### Outbound (Topics)
The service broadcasts information to specific topics that driver clients subscribe to:
* **`/topic/ride-assignment`**: Sends new ride requests to eligible drivers.
* **`/topic/trip-updates`**: Provides real-time status updates regarding an active trip.

---

## Configuration & Databases
* **PostgreSQL**: Configured via `application.properties` with the datasource URL `jdbc:postgresql://localhost:5432/mydb`.
* **JPA**: Uses Hibernate with `ddl-auto=update` for automated schema management.
* **Server Setup**: The service runs on port `8081` by default.
* **WebSocket Config**: Defines the `/ws-driver` endpoint for STOMP connections and enables a simple message broker on `/topic`.
## Key Components
* **DriverMatchingClientService**: Logic for initiating the WebSocket connection and sending updates to the Matching Service.
* **TopicListenerService**: Manages the subscriptions to various topics once the WebSocket session is active.
* **DriverStompSessionHandler**: Implements the STOMP session lifecycle, including handling transport errors and frame conversion.