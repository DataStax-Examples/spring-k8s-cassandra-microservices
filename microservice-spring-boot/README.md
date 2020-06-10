# Spring Boot REST service with Cassandra and Kubernetes

## Run on Kubernetes (minikube)

Start a minikube cluster with RBAC authorization mode
```
minikube start --extra-config=apiserver.authorization-mode=RBAC,Node
```

Use minikube's Docker daemon to avoid needing to use a registry
```
eval `minikube docker-env`
```

Build the service and docker image
```
mvn package
```
```
docker build -t csplinter/spring-boot-service:1.0.0-SNAPSHOT .
```

Create Kubernetes namespace called `spring-boot-service`
```
kubectl create namespace spring-boot-service
```

Deploy the application
```
kubectl apply -n spring-boot-service -f ../deploy/spring-boot
```

Check that things are looking good
```
minikube dashboard
```

```
kubectl -n spring-boot-service get pods
```

```
kubectl -n spring-boot-service logs -f <pod-name>
```

### Access the endpoints

Get the URL of the NodePort of the service
```
minikube -n spring-boot-service service spring-boot-service --url
```
You will see output that looks similar to the following
```
🏃  Starting tunnel for service spring-boot-service.
|---------------------|---------------------|-------------|------------------------|
|      NAMESPACE      |        NAME         | TARGET PORT |          URL           |
|---------------------|---------------------|-------------|------------------------|
| spring-boot-service | spring-boot-service |             | http://127.0.0.1:59165 |
|---------------------|---------------------|-------------|------------------------|
```

Test the endpoints with curl

Add products
```
curl -X POST -H "Content-Type: application/json" -d '{"name": "mobile", "id":"123e4567-e89b-12d3-a456-556642440000", "description":"iPhone", "price":"500.00"}' <replace-with-url>/api/products/add
curl -X POST -H "Content-Type: application/json" -d '{"name": "mobile", "id":"123e4567-e89b-12d3-a456-556642440001", "description":"Android", "price":"600.00"}' <replace-with-url>/api/products/add
```

Get products with name = mobile
```
curl <replace-with-url>/api/products/search/mobile
```

Get products with name = mobile and id = 123e4567-e89b-12d3-a456-556642440001
```
curl <replace-with-url>/api/products/search/mobile/123e4567-e89b-12d3-a456-556642440001
```

Delete product with name = mobile and id = 123e4567-e89b-12d3-a456-556642440001
```
curl -X DELETE <replace-with-url>/api/products/delete/mobile/123e4567-e89b-12d3-a456-556642440001
```