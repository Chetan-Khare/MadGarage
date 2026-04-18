# --- Build Stage ---
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

# --- Run Stage ---
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Environment variable defaults
ENV UPLOAD_DIR=/app/storage
ENV SPRING_PROFILES_ACTIVE=prod

# Create storage directory for persistent uploads
RUN mkdir -p /app/storage

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
