ARG ARCH=
FROM ${ARCH}maven as build
WORKDIR /srv/package

COPY ./ ./

RUN mvn clean package -Pprod -Dmaven.test.skip=true

FROM ${ARCH}openjdk:11
WORKDIR /srv/package
COPY ./sdp /srv/package/sdp
COPY --from=build /srv/package/target/lib /srv/package/lib
COPY --from=build /srv/package/target/RTSPServer*-SNAPSHOT.jar /srv/package/RTSPServer.jar
ENV SDP_FILE=/srv/package/sdp/kareoke.sdp
EXPOSE 8080

CMD java -Xmx 1G -jar  ./RTSPServer.jar
