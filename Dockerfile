# Use an official Maven image as the base image
FROM maven:3.8.4-openjdk-17-slim AS build
# Set the working directory in the container
WORKDIR /app

COPY pom.xml .
COPY src ./src

# Build the application using Maven
RUN mvn clean package

# Use an official OpenJDK image as the base image
FROM openjdk:21-slim
# Set the working directory in the container
WORKDIR /app

# Copy the built JAR file from the previous stage to the container
COPY --from=build /app/target/springboot-0.0.1-SNAPSHOT.jar app.jar

# Set the entry point to run your application
ENTRYPOINT ["java","-jar","/app/app.jar"]