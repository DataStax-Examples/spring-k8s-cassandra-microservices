# Spring Data Cassandra REST API

This sample uses Spring Data Cassandra and DataStax Astra to build a REST API for a backend service that interacts with products in orders.

## Running

```
ASTRA_DB_BUNDLE=<path-to-secure-connect-bundle>
ASTRA_DB_USERNAME=<astra-username>
ASTRA_DB_PASSWORD=<astra-password>
```

```
mvn package
```

```
java -jar target/spring-data-example-1.0.0-SNAPSHOT.jar
```


## API

After starting the app, go to this link in a browser: http://localhost:8081/swagger-ui/

![Swagger](https://github.com/DataStax-Examples/spring-k8s-cassandra-microservices/blob/spring-data-starter/doc/pics/spring-data-swagger-api.png?raw=true)


![Cassandra Spring](https://github.com/DataStax-Examples/spring-k8s-cassandra-microservices/blob/spring-data-starter/doc/pics/cassandra-spring.png?raw=true)
