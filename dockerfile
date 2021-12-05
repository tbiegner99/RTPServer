ARG ARCH=
FROM ${ARCH}maven as build
WORKDIR /srv/package

COPY ./ ./

RUN mvn clean package -Pprod -Dmaven.test.skip=true

FROM ${ARCH}openjdk:11
WORKDIR /srv/package
COPY --from=build /srv/package/target/lib /srv/package//lib
COPY --from=build /srv/package/target/RTSPServer*-SNAPSHOT.jar /srv/package/RTSPServer.jar
EXPOSE 8080

CMD java -jar  ./RTSPServer.jar
