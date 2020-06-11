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

Forward the local port to that of the service
```
kubectl -n spring-boot-service get pods
kubectl -n spring-boot-service port-forward <pod-name> 8083:8083
```

Test the endpoints with curl

Add products
```
curl -X POST -H "Content-Type: application/json" \
-d '{"name": "mobile", "id":"123e4567-e89b-12d3-a456-556642440000", "description":"iPhone", "price":"500.00"}' http://localhost:8083/api/products/add

curl -X POST -H "Content-Type: application/json" \
-d '{"name": "mobile", "id":"123e4567-e89b-12d3-a456-556642440001", "description":"Android", "price":"600.00"}' http://localhost:8083/api/products/add
```

Get products with name = mobile
```
curl http://localhost:8083/api/products/search/mobile
```

Get products with name = mobile and id = 123e4567-e89b-12d3-a456-556642440001
```
curl http://localhost:8083/api/products/search/mobile/123e4567-e89b-12d3-a456-556642440001
```

Delete product with name = mobile and id = 123e4567-e89b-12d3-a456-556642440001
```
curl -X DELETE http://localhost:8083/api/products/delete/mobile/123e4567-e89b-12d3-a456-556642440001
```