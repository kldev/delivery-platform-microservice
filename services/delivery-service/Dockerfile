FROM gradle:9.7.1-jdk25 AS build

WORKDIR /app

COPY ./services ./services
COPY ./libraries ./libraries
COPY build.gradle.kts .
COPY settings.gradle.kts .

RUN gradle clean :services:delivery-service:bootJar --no-daemon

FROM eclipse-temurin:25-jre

WORKDIR /app

COPY --from=build /app/services/delivery-service/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]