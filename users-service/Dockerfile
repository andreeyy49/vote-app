FROM openjdk:17-oracle

WORKDIR /users-service

COPY target/users-service-0.0.1-SNAPSHOT.jar user-service.jar

CMD ["java", "-jar", "user-service.jar"]