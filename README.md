# Spring Boot Core: Maven, Config, and IoC Principles

This guide covers the fundamental building blocks of a Spring Boot application, focusing on dependency management, the IoC container, and essential annotations for dual-database setups.

---

## 1. The Core `pom.xml`
The Project Object Model (POM) file is the source of truth for your Maven project. It defines the project's identity and its dependencies.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="[http://maven.apache.org/POM/4.0.0](http://maven.apache.org/POM/4.0.0)"
         xmlns:xsi="[http://www.w3.org/2001/XMLSchema-instance](http://www.w3.org/2001/XMLSchema-instance)"
         xsi:schemaLocation="[http://maven.apache.org/POM/4.0.0](http://maven.apache.org/POM/4.0.0) [http://maven.apache.org/xsd/maven-4.0.0.xsd](http://maven.apache.org/xsd/maven-4.0.0.xsd)">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>

    <groupId>com.example</groupId>
    <artifactId>spring-data-demo</artifactId>
    <version>1.0.0</version>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <scope>runtime</scope>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-mongodb</artifactId>
        </dependency>

        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

# Spring Boot Core: IoC, Annotations, and Configuration

## 2. Inversion of Control (IoC) & Dependency Injection (DI)

### What is IoC?
In traditional Java programming, the developer controls the flow of the program and manually instantiates classes (e.g., `UserService service = new UserService()`). In Spring, this control is **inverted**. The **Spring Container (ApplicationContext)** takes responsibility for managing the lifecycle of objects (called **Beans**), from instantiation to destruction.



### What is Dependency Injection (DI)?
DI is the design pattern used to implement IoC. Instead of a class creating its own dependencies, the container "injects" them at runtime.

* **Loose Coupling:** Classes don't need to know how to construct their dependencies.
* **Testability:** You can easily swap real database repositories with "Mock" objects during unit testing.
* **Memory Management:** Spring manages Beans as Singletons by default, ensuring efficient resource use.

---

## 3. Essential Spring Annotations

Spring uses annotations to identify which classes should be managed as Beans and how they should behave.

### Stereotype Annotations (The "What")
* **`@Component`**: The generic marker for any Spring-managed component.
* **`@Service`**: A specialization of `@Component` used for business logic.
* **`@Repository`**: Used for the Data Access Layer. It handles database communication and translates vendor-specific exceptions.
* **`@RestController`**: Marks a class as a web controller where every method returns data (JSON) directly to the body of the web response.

### Data & Mapping Annotations
* **`@Entity`**: (JPA) Maps a Java class to a **PostgreSQL** table.
* **`@Document`**: (MongoDB) Maps a Java class to a **MongoDB** collection.
* **`@Id`**: Specifies the primary key of the entity or document.

### Wiring Annotations (The "How")
* **`@Autowired`**: Instructs Spring to find a matching Bean in the container and inject it into the field, constructor, or setter.
  > **Note:** **Constructor Injection** is the industry-standard recommendation over Field Injection for better code reliability.

---

## 4. Configuration Philosophy

Spring Boot follows the principle of **"Convention over Configuration."**

### Auto-Configuration
If Spring Boot detects `postgresql` and `spring-boot-starter-data-mongodb` in your `pom.xml`, it automatically attempts to configure the necessary connection factories. You only need to provide the specific credentials in your properties file.

### `application.properties` Example
Located in `src/main/resources/`, this file overrides the default settings:

```properties
# App Metadata
spring.application.name=hybrid-data-app

# PostgreSQL Connection (JDBC/JPA)
spring.datasource.url=jdbc:postgresql://localhost:5432/user_db
spring.datasource.username=postgres
spring.datasource.password=password123

# MongoDB Connection
spring.data.mongodb.uri=mongodb://localhost:27017/logs_db
```

---

## 5. Summary: Relational vs. NoSQL Setup

When building a hybrid application, it is important to understand the technical differences between how Spring Data handles PostgreSQL (via JPA) and MongoDB.

| Feature | Relational (PostgreSQL) | NoSQL (MongoDB) |
| :--- | :--- | :--- |
| **Spring Starter** | `spring-boot-starter-data-jpa` | `spring-boot-starter-data-mongodb` |
| **Primary Annotation** | `@Entity` | `@Document` |
| **Base Repository** | `JpaRepository<T, ID>` | `MongoRepository<T, ID>` |
| **Query Language** | JPQL / Native SQL | MongoDB Query Language (MQL) |
| **Schema Type** | Rigid / Structured | Dynamic / Schema-less |
| **Transactionality** | Full ACID Compliance | Multi-document (since v4.0) |



---

## 6. Implementation Checklist

To ensure your hybrid setup works correctly, follow this standard workflow:

1.  **Package Separation**: Keep your JPA Entities and MongoDB Documents in separate packages (e.g., `com.app.jpa.models` and `com.app.mongo.models`).
2.  **Enable Repositories**: Use `@EnableJpaRepositories` and `@EnableMongoRepositories` on your Main class to point Spring to the correct folders.
3.  **Define Identifiers**: Use `@Id` for both. Note that JPA usually uses `Long` or `UUID` (Auto-increment), while MongoDB typically uses `String` (ObjectId).
4.  **Service Integration**: Inject both repository types into a single `@Service` class to coordinate data flow between the two databases.

---

## 7. Troubleshooting Common Issues

* **Bean Ambiguity**: If Spring gets confused between the two data sources, ensure you have explicitly defined the `basePackages` in your configuration.
* **Driver Missing**: Ensure the `postgresql` driver is included in the `pom.xml`; the JPA starter provides the logic, but not the specific driver for Postgres.
* **Connection Timeouts**: Check your `application.properties` to ensure the MongoDB URI and JDBC URL are reachable from your local environment.

---

# Spring Boot Annotations Guide

Annotations are metadata tags that provide instructions to the Spring Framework. They eliminate the need for boilerplate code and XML configuration.

---

## 1. Core Framework Annotations

These manage the **Inversion of Control (IoC)** container and **Dependency Injection (DI)**.

* **`@SpringBootApplication`**: The entry point. It triggers auto-configuration, component scanning, and allows you to define extra configuration beans.
* **`@Autowired`**: Tells Spring to inject a dependency automatically.
  * *Best Practice:* Use **Constructor Injection** instead of field injection for better testability.
* **`@Bean`**: Used within `@Configuration` classes to manually register an object as a Bean in the Spring Context.



---

## 2. Stereotype Annotations (Component Management)

These tell Spring, "Create an instance of this class and manage it for me."

| Annotation | Layer | Purpose |
| :--- | :--- | :--- |
| **`@Component`** | General | The parent annotation for any managed Bean. |
| **`@Service`** | Business | Holds business logic and service-layer calculations. |
| **`@Repository`** | Persistence | Accesses the database; provides automatic exception translation. |
| **`@RestController`** | Presentation | Handles HTTP requests and returns data (JSON/XML). |

---

## 3. Data & Database Mapping

Since your project uses both **PostgreSQL** and **MongoDB**, you must distinguish between JPA and NoSQL annotations.

### PostgreSQL (JPA/Hibernate)
* **`@Entity`**: Defines the class as a persistent database table.
* **`@Table`**: Specifies the table name in PostgreSQL.
* **`@Id`**: Marks the primary key.
* **`@GeneratedValue`**: Defines the strategy for ID generation (e.g., `IDENTITY` or `SEQUENCE`).

### MongoDB (NoSQL)
* **`@Document`**: Maps the class to a MongoDB collection.
* **`@Id`**: Marks the field used as the `_id` in MongoDB.
* **`@Field`**: Optional; used to define a specific name for a key in the document.

---

## 4. Lombok Annotations (Boilerplate Reduction)

Lombok handles the "boring" parts of Java during compilation so your code remains clean.

* **`@Data`**: A "combo" annotation. It generates `@ToString`, `@EqualsAndHashCode`, `@Getter`, `@Setter`, and `@RequiredArgsConstructor`.
* **`@NoArgsConstructor`**: Creates a constructor with no arguments (Required by JPA).
* **`@AllArgsConstructor`**: Creates a constructor for every field in the class.
* **`@Builder`**: Implements the Builder Pattern for object creation.
* **`@Slf4j`**: Provides a logger instance (e.g., `log.info("Started");`).

---

## 5. Practical Implementation Example

Here is a typical class using these annotations in harmony:

```java
@Document(collection = "products") // MongoDB mapping
@Data                              // Lombok: Getters/Setters/ToString
@NoArgsConstructor                 // Required for persistence
@AllArgsConstructor                // For easy object creation
public class Product {
    @Id
    private String id;
    private String name;
    private Double price;
}
```

```java
@Service                           // Spring Service Layer
@Slf4j                             // Lombok Logging
@RequiredArgsConstructor           // Auto-injects final fields via constructor
public class ProductService {
    
    private final ProductRepository repository; // Final field injected by Lombok

    public void saveProduct(Product p) {
        log.info("Saving product: {}", p.getName());
        repository.save(p);
    }
}
```

## 8. Conclusion

By combining **PostgreSQL** and **MongoDB**, you gain the best of both worlds: the strict consistency and relational integrity of SQL for user accounts and transactions, and the high-speed, flexible document storage of NoSQL for logs, catalogs, or real-time data.