# --------- Stage 1: Build ---------
FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests

# --------- Stage 2: Runtime ---------
FROM eclipse-temurin:21

WORKDIR /app

# Copy built jar to the working directory
COPY --from=builder /app/target/task-manger-api-*.jar app.jar

ENV SPRING_PROFILES_ACTIVE=prod

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

