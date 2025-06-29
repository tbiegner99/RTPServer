# Use Maven to build the project in a build stage
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml ./
COPY src ./src
COPY sdp ./sdp
COPY Application.properties ./Application.properties
RUN mvn clean package -DskipTests

# Use Eclipse Temurin OpenJDK 17 as the runtime image
FROM eclipse-temurin:17-jre
WORKDIR /app
# Copy the built JAR from the build stage
COPY --from=build /app/target/RTSPServer-1.0-SNAPSHOT.jar ./RTSPServer.jar

ENV PORT=4586
ENV HTTP_PORT=8001

EXPOSE 4586
EXPOSE 8001

ENTRYPOINT ["java", "-jar", "RTSPServer.jar"]

