FROM adoptopenjdk/maven-openjdk11 as build

COPY ./ ./

RUN mvn clean package -Pprod -Dmaven.test.skip=true

FROM adoptopenjdk/openjdk11
COPY --from=build ./target/lib ./target/lib
COPY --from=build ./target/RTSPServer*-SNAPSHOT.jar ./RTSPServer.jar
EXPOSE 8080

CMD java -jar  ./RTSPServer.jar
