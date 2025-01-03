FROM gradle:8.11.1-jdk17 AS build

WORKDIR /app

COPY build.gradle settings.gradle ./

RUN gradle build --no-daemon

COPY src /app/src

RUN gradle build --no-daemon

FROM openjdk:17-jdk-slim

WORKDIR /app

COPY --from=build /app/build/libs/coupons-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
