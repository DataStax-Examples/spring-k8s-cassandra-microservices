# Spring Data REST service with Cassandra and Kubernetes

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
docker build -t csplinter/spring-data-service:1.0.0-SNAPSHOT .
```

Create Kubernetes namespace called `spring-data-service`
```
kubectl create namespace spring-data-service
```

Deploy the application
```
kubectl apply -n spring-data-service -f ../deploy/spring-data
```

Check that things are looking good
```
minikube dashboard
```

```
kubectl -n spring-data-service get pods
```

```
kubectl -n spring-data-service logs -f <pod-name>
```

### Access the endpoints

Get the URL of the NodePort of the service
```
minikube -n spring-data-service service spring-data-service --url
```
You will see output that looks similar to the following
```
🏃  Starting tunnel for service spring-data-service.
|---------------------|---------------------|-------------|------------------------|
|      NAMESPACE      |        NAME         | TARGET PORT |          URL           |
|---------------------|---------------------|-------------|------------------------|
| spring-data-service | spring-data-service |             | http://127.0.0.1:59165 |
|---------------------|---------------------|-------------|------------------------|
```

Test the endpoints with curl

Add an order
```
curl -H "Content-Type: application/json" -d '{"key": {"orderId":"123e4567-e89b-12d3-a456-556642440000", "productId":"123e4567-e89b-12d3-a456-556642440000"}, "productName":"iPhone", "productPrice":"500.00", "productQuantity":1, "addedToOrderTimestamp": "2020-04-12T11:21:59.001+0000"}' <replace-with-url>/api/orders/add
curl -H "Content-Type: application/json" -d '{"key": {"orderId":"123e4567-e89b-12d3-a456-556642440000", "productId":"123e4567-e89b-12d3-a456-556642440001"}, "productName":"Android", "productPrice":"600.00", "productQuantity":1, "addedToOrderTimestamp": "2020-04-12T11:22:59.001+0000"}' <replace-with-url>/api/orders/add
```

Get orders with order_id = 123e4567-e89b-12d3-a456-556642440000
```
curl <replace-with-url>/api/orders/search/order-by-id?orderId=123e4567-e89b-12d3-a456-556642440000
```
Get order with order_id = 123e4567-e89b-12d3-a456-556642440000 and product_id = 123e4567-e89b-12d3-a456-556642440000
```
curl "<replace-with-url>/api/orders/search/order-by-product-id?orderId=123e4567-e89b-12d3-a456-556642440000&productId=123e4567-e89b-12d3-a456-556642440000"
```
Get only the product name and price of order_id = 123e4567-e89b-12d3-a456-556642440000
```
curl <replace-with-url>/api/orders/search/name-and-price-only?orderId=123e4567-e89b-12d3-a456-556642440000
```
Shows how to use a projection with Spring Data REST
```
curl "<replace-with-url>/api/orders/search/name-and-price-only?orderId=123e4567-e89b-12d3-a456-556642440000&projection=product-name-and-price"
```

Delete order with order_id = 123e4567-e89b-12d3-a456-556642440000 and product_id = 123e4567-e89b-12d3-a456-556642440000
```
curl -X DELETE "<replace-with-url>/api/orders/delete/product-from-order?orderId=123e4567-e89b-12d3-a456-556642440000&productId=123e4567-e89b-12d3-a456-556642440000"
```

Delete order with order_id = 123e4567-e89b-12d3-a456-556642440000
```
curl -X DELETE "<replace-with-url>/api/orders/delete/order?orderId=123e4567-e89b-12d3-a456-556642440000"```
```