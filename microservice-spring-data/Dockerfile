FROM openjdk:11.0.7-jre-slim-buster
EXPOSE 8081
EXPOSE 8082
WORKDIR /app
ARG JAR=microservice-spring-data-1.0-SNAPSHOT.jar

COPY /target/$JAR /app.jar
ENTRYPOINT ["java","-jar","/app.jar"]