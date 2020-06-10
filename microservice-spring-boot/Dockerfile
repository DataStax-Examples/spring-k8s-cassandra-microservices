FROM openjdk:11.0.7-jre-slim-buster
EXPOSE 8083
EXPOSE 8084
WORKDIR /app
ARG JAR=microservice-spring-boot-1.0-SNAPSHOT.jar

COPY /target/$JAR /app.jar
ENTRYPOINT ["java","-jar","/app.jar"]