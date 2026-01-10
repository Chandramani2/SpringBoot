# 📌 FirstClub Membership Program

FirstClub is a scalable membership management system built with Spring Boot and PostgreSQL. It utilizes a **Master Plan Matrix** to handle combinations of Membership Tiers and Subscription Plans, ensuring high data integrity and concurrency protection using JPA Optimistic Locking.

---

## 🛠 Admin & Data Population
These APIs are used to initialize the system catalog and seed initial user data.

### 1. Setup Master Data & Users
* **Endpoint:** `POST /api/admin/data/setup-complete-data`
* **Description:** Performs an **Upsert** (Update or Insert) for the entire system.
    * Initializes 4 configurable Tiers (NONE, SILVER, GOLD, PLATINUM).
    * Generates a 10-plan Subscription Matrix (Tier + PlanType combinations).
    * Creates 20 users and links them to the default `NONE-NONE` plan.
* **Request Body (JSON List):**
```json
[
  { "name": "Aarav Sharma", "email": "aarav.s@firstclub.com", "phoneNumber": "9876500001" },
  { "name": "Aditi Verma", "email": "aditi.v@firstclub.com", "phoneNumber": "9876500002" }
]
```
# 💳 Membership Management

Functional endpoints for managing the user subscription lifecycle, tracking status, and handling plan transitions.

[Image of Spring Boot REST Controller architecture for membership management]

---

### 1. Get Available Tiers
**Endpoint:** `GET /api/membership/tiers`  
**Description:** Retrieves all membership tiers defined in the system along with their configurable perks (e.g., discount percentages, free delivery eligibility).

**cURL:**
```bash
curl --location 'http://localhost:8080/api/membership/tiers'
```

### 2. Subscribe to a Plan
**Endpoint:** `POST /api/membership/subscribe`  
**Description:** Transitions a user from the default `NONE` state to an active membership. This API updates the user's `subscription_id` reference to a specific pre-defined combination in the master catalog and calculates the `expiryDate` from the current timestamp.

**Query Parameters:**
* `userId` (Long): The ID of the user.
* `tier` (Enum): `SILVER`, `GOLD`, `PLATINUM`.
* `plan` (Enum): `MONTHLY`, `QUARTERLY`, `YEARLY`.

**cURL:**
```bash
curl --location --request POST 'http://localhost:8080/api/membership/subscribe?userId=1&tier=GOLD&plan=YEARLY'
```
### 3. Change Membership Tier
**Endpoint:** `PUT /api/membership/change-tier`  
**Description:** Upgrades or downgrades a user's membership level. The system maintains the user's existing billing cycle (Plan Type) but switches the reference to the new requested Tier in the master catalog.

**Query Parameters:**
* `userId` (Long): The unique identifier of the user.
* `newTier` (Enum): The target tier name (`SILVER`, `GOLD`, `PLATINUM`).

**cURL:**
```bash
curl --location --request PUT 'http://localhost:8080/api/membership/change-tier?userId=1&newTier=PLATINUM'
```

### 4. Track Membership Details
**Endpoint:** `GET /api/membership/track/{userId}`  
**Description:** Fetches the complete membership profile of a user. This includes the current subscription plan, the associated tier perks (discounts, delivery benefits), and the calculated expiry date.

[Image of Spring Boot REST API response JSON structure for membership details]

**Technical Detail:** To prevent `LazyInitializationException` (Proxy "no session" error), this endpoint uses a **Join Fetch** query. This loads the `User`, `Subscription`, and `Tier` entities in a single SQL execution rather than multiple lazy-loaded calls.

**cURL:**
```bash
curl --location 'http://localhost:8080/api/membership/track/1'
```

### 5. Cancel Subscription
**Endpoint:** `DELETE /api/membership/cancel/{userId}`  
**Description:** Terminates the user's active paid subscription and reverts them to the baseline state. The system performs the following operations:
* Sets the user's `MembershipStatus` to `INACTIVE`.
* Reverts the user's `subscription_id` reference back to the global `NONE-NONE` master plan.
* Resets the `expiryDate` to the current timestamp to signify the end of the paid term.

**cURL:**
```bash
curl --location --request DELETE 'http://localhost:8080/api/membership/cancel/1'
```

### 6. Trigger Concurrency Test
**Endpoint:** `POST /api/membership/test-concurrency/{userId}`  
**Description:** A diagnostic API designed to simulate a race condition where two simultaneous threads attempt to update the same user's plan. This is used to verify the system's data integrity and conflict resolution strategies.

[Image of Java concurrency race condition and optimistic locking flow]

**Technical Detail:** * This validates the **Optimistic Locking** mechanism by using the `@Version` field in the `User` entity.
* You can observe the SQL in the logs: `UPDATE users SET ..., version = version + 1 WHERE id = ? AND version = ?`.
* If one thread commits first, the second thread fails the version check, catches an `ObjectOptimisticLockingFailureException`, and triggers the **Retry Logic** (up to 3 attempts).

**cURL:**
```bash
curl --location --request POST 'http://localhost:8080/api/membership/test/concurrency/1'
```

## 📅 Expiry Calculation Logic

The `expiryDate` is dynamically calculated by adding the selected plan's duration to the `LocalDateTime.now()` at the exact moment of the transaction. This ensures that the user's membership is always relative to the time of purchase or renewal.



| Plan Type | Duration Added | Logic Description |
| :--- | :--- | :--- |
| **MONTHLY** | + 1 Month | Standard monthly billing cycle. |
| **QUARTERLY** | + 3 Months | Mid-term billing cycle with slight discount potential. |
| **YEARLY** | + 1 Year | Annual billing cycle for long-term commitment. |
| **NONE** | + 100 Years | Effectively infinite; used for the permanent base plan. |

---

## ⚙️ Configuration for Logging

To observe the versioning and thread behavior "behind the scenes," especially when testing the concurrency API, add these properties to your `application.properties` file. This allows you to track the `@Version` increment in the SQL `UPDATE` statements.



```properties
# Enable SQL logging to see @Version checks
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
logging.level.org.hibernate.orm.jdbc.bind=trace
```

### Understanding the Logs

When the concurrency test runs, you can monitor your IDE console to see the internal SQL transactions. Hibernate handles the `@Version` check by adding the current version to the `WHERE` clause of every `UPDATE` statement.



**Example SQL Log:**
```sql
-- Thread-1 attempts to update
UPDATE users 
SET current_plan_id = 3, version = 6 
WHERE id = 1 AND version = 5;
```

* **SET version = 6**: Hibernate automatically increments the version for the new state.
* **WHERE version = 5**: The update will only succeed if the record in the database still has version 5.
* **Outcome**: If another thread changed the version to 6 while Thread-1 was processing, the `WHERE` clause fails to find a match. Consequently, 0 rows are updated, and Hibernate throws a conflict exception.

---

## 🛡️ Concurrency Handling Logic

The system is designed to handle "Lost Update" scenarios—where two processes try to update the same user record at the exact same time.



### 1. Optimistic Locking Strategy
We use **Optimistic Locking** instead of "Pessimistic Locking" (which would freeze the table).
* The system assumes conflicts are rare and allows multiple threads to read the data simultaneously.
* It only checks for a collision at the final moment of the `UPDATE` query.

### 2. The Retry Mechanism
In the `MembershipService`, we implemented a **Retry Loop** to handle these collisions gracefully:

* **Detection**: If the database update returns 0 rows, Spring throws an `ObjectOptimisticLockingFailureException`.
* **Catch & Reload**: The service catches this error, triggers a short "backoff" (100ms), and then re-fetches the user from the database. This ensures the second attempt is working with the *most recent* version number.
* **Max Attempts**: The logic repeats up to **3 times**.



### 3. Benefits of this Approach
* **Scalability**: Thousands of users can check their status simultaneously without blocking each other.
* **Accuracy**: Prevents "Dirty Writes" where one change might be accidentally overwritten by another.
* **Stability**: The retry logic ensures that most collisions are resolved automatically in the background without bothering the end user.

---
