FROM eclipse-temurin:25-jre-alpine
ARG JAR_FILE=target/*.jar
COPY ./target/MembershipProgram-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
