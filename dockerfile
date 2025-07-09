# Use Maven to build the project in a build stage
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml ./
COPY src ./src
COPY sdp ./sdp
COPY ./src/main/resources/Application.properties ./Application.properties
RUN mvn clean package -DskipTests

# Use Eclipse Temurin OpenJDK 17 as the runtime image
FROM eclipse-temurin:17-jre
WORKDIR /app
# Copy the built JAR from the build stage
COPY sdp ./sdp
COPY --from=build /app/target/RTSPServer-1.0-SNAPSHOT-jar-with-dependencies.jar ./RTSPServer.jar

ENV PORT=4586
ENV HTTP_PORT=8001
ENV SDP_FILE=/app/sdp/kareoke.sdp

EXPOSE 4586
EXPOSE 8001
RUN apt-get update && \
    apt-get install -y iputils-ping && \
    apt-get install -y net-tools
RUN apt-get install -y nmap && \
    apt-get install -y iproute2 && \
    apt-get install -y curl    
RUN apt-get install -y netcat-traditional

ENTRYPOINT ["java", "-jar", "RTSPServer.jar"]

