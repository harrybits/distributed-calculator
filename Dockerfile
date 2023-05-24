FROM openjdk:17
MAINTAINER harrybits
COPY target/dcalc-1.0-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]

EXPOSE 80