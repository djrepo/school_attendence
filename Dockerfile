FROM openjdk:8-jdk-alpine
MAINTAINER dalibor.jacko@gmail.com
COPY ./target target
ENTRYPOINT ["java","-jar","enrollment-be-0.0.1-SNAPSHOT.jar"]