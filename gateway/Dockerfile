FROM openjdk:17-jdk-slim

WORKDIR /gateway

COPY target/gateway-0.0.1-SNAPSHOT.jar gateway-service.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "gateway-service.jar"]