FROM maven:3.8.4-openjdk-17 as build

RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app

COPY pom.xml /usr/src/app
RUN mvn dependency:go-offline -B

COPY src /usr/src/app/src
RUN mvn clean package

FROM openjdk:17-jdk-alpine

LABEL maintainer="example@example.com"

VOLUME /tmp

EXPOSE 8080

COPY --from=build /usr/src/app/target/*.jar app.jar

ENTRYPOINT ["java","-jar","/app.jar"]