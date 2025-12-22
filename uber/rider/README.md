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

# Rider Service Documentation

## Overview
The **Rider Service** is the primary interface for passengers within the ride-sharing platform. It manages user profiles, handles ride requests, coordinates with the Matching Service to find drivers, and processes payment records.

---

## Technical Stack
* **Java**: Version 25.
* **Framework**: Spring Boot 4.0.0.
* **Database**: PostgreSQL (relational database used for persistence).
* **Communication**:
    * **REST**: Used for synchronous communication with the Matching Service.
    * **WebSockets (STOMP)**: Facilitates real-time asynchronous communication with the Driver Service.
* **Utilities**: Lombok for reducing boilerplate and Jackson for JSON processing.

---

## Core Models
* **User**: Manages passenger profiles, including `userName`, `phoneNumber`, `paymentPending` balance, `status`, and `userLocation`.
* **Ride**: Tracks journey details, including `riderId`, `driverId`, `pickUp` and `destination` locations, `rideStatus`, `estimatedFare`, and `surgeMultiplier`.
* **Payment**: A data object for recording transaction details such as `amount`, `paymentMethod`, and `paymentStatus`.

---

## API Endpoints

### Ride Management (`/v1/rides`)
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| **POST** | `/v1/rides` | Initiates the matching process and creates a ride record. |
| **GET** | `/v1/rides/{id}` | Retrieves the current status of a specific ride. |
| **POST** | `/v1/rides/{id}/initiate` | Manually triggers the matching engine for a specific ride. |

### User Management (`/v1/user`)
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| **GET** | `/v1/user` | Retrieves a list of all registered passengers. |
| **GET** | `/v1/user/{id}` | Fetches profile details for a specific user. |
| **POST** | `/v1/user/create` | Registers a new passenger in the system. |
| **POST** | `/v1/user/createList` | Allows for bulk registration of multiple users. |

### Payment (`/payments`)
* **Base URL**: `/payments` (Handled by `PaymentController` for recording and processing trip payments).

---

## WebSocket & Real-time Communication
The service utilizes a WebSocket client to maintain a live connection with the **Driver Service**.

### Outbound Communication
The `RiderDriverClientService` connects to the URL defined by `app.driver.websocket.url`.
* **Destination**: `/app/driver.updateLocation`.
* **Function**: Sends location-based payloads to the driver infrastructure.

### Inbound Topics (Subscriptions)
The `RiderStompSessionHandler` manages subscriptions to receive real-time updates from the Driver Service:
* **`/topic/ride-update-status`**: Listens for changes in ride status, such as when a driver accepts a request.
* **`/topic/driver-rider-location`**: Receives live location updates for relevant participants.
* **`/topic/driver-reached-location`**: Alerts the system when a driver arrives at a pickup or destination point.

---

## Configuration & Databases
* **PostgreSQL**: Configured in `application.properties` with the datasource URL `jdbc:postgresql://localhost:5432/mydb`.
* **JPA**: Uses Hibernate with `ddl-auto=update` for schema management.
* **Service URLs**: Defines internal locations for the Matching Service (`http://localhost:8082`) and the Driver Service (`http://localhost:8081`).
* **Broker Configuration**: The service enables a simple memory-based message broker on `/topic` and sets the application prefix to `/app`.