FROM gradle:9.7.1-jdk25 AS build

WORKDIR /app

COPY ./services ./services
COPY ./libraries ./libraries
COPY build.gradle.kts .
COPY settings.gradle.kts .
RUN gradle dependencies
RUN gradle :services:notifications-service:quarkusBuild --no-daemon

FROM eclipse-temurin:25-jre

WORKDIR /app
ENV QUARKUS_HTTP_HOST=0.0.0.0
ENV QUARKUS_PROFILE=prod
COPY --from=build /app/services/notifications-service/build/quarkus-app/ ./

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "quarkus-run.jar"]