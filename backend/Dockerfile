# Use an official Maven image to build the application
FROM maven:3.8.5-openjdk-17 as build

# Set the working directory inside the container
WORKDIR /app

# Copy the pom.xml file and the src directory to the working directory
COPY pom.xml .
COPY src ./src

# Package the application (this will also download dependencies)
RUN mvn clean package -DskipTests

# Use an official OpenJDK runtime as a parent image for the final stage
FROM openjdk:17-jdk

# Set the working directory inside the container
WORKDIR /app

# Copy the jar file from the build stage
COPY --from=build /app/target/magmutual-takehome-project-0.0.1-SNAPSHOT.jar /app/app.jar

# Make port 8080 available to the world outside this container
EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java","-jar","/app/app.jar"]
