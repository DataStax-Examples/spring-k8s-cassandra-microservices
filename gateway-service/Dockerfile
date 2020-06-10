FROM openjdk:11.0.7-jre-slim-buster
EXPOSE 8080
EXPOSE 8085
WORKDIR /app
ARG JAR=gateway-service-1.0-SNAPSHOT.jar

COPY /target/$JAR /app.jar
ENTRYPOINT ["java","-jar","/app.jar"]