ARG ARCH=
FROM ${ARCH}maven as build
WORKDIR /srv/package
COPY ./ ./

RUN mvn clean package -Pprod -Dmaven.test.skip=true

FROM ${ARCH}openjdk:11

COPY --from=build ./target/lib ./lib
COPY --from=build ./target/RTSPServer*-SNAPSHOT.jar ./RTSPServer.jar
EXPOSE 8080

CMD java -jar  ./RTSPServer.jar
