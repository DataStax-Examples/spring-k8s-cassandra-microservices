# Microservices with Spring, Kubernetes, and Cassandra

This repository contains sample inventory microservices to demonstrate how to use Spring, Kubernetes and Cassandra together a single stack.

![Arch link](https://github.com/DataStax-Examples/spring-k8s-cassandra-microservices/blob/master/doc/pics/spring-k8s-cassandra-small.png?raw=true)

#### Contributors: 
- [Cedrick Lunven](https://github.com/clun) - twitter handdle [@clun](https://twitter.com/clunven)
- [Chris Splinter](https://github.com/csplinter)
- [Frank Moley](https://github.com/fpmoles) - twitter handle [@fpmoles](https://twitter.com/fpmoles)

#### Modules:
- [`microservice-spring-boot`](microservice-spring-boot): Service for Products
   - **Persistence Layer** : uses Cassandra Java driver's `CqlSession` directly for queries to products table
   - **Exposition Layer** : uses `spring-web`  `@Controller`
- [`microservice-spring-data`](microservice-spring-data): Service for Orders
  - **Persistence Layer** : uses Spring Data Cassandra for data access to orders table
  - **Exposition Layer** : uses Spring Data REST for API generation
- [`gateway-service`](gateway-service): Spring Cloud Gateway to route to the microservices

## 1. Objectives

Show a working set of microservices illustrating how to build Spring microservices with Kubernetes and Cassandra.
This repo leverages Spring modules:
- `spring-data`
- `spring-boot`
- `spring-data-rest`
- `spring-web`
- `spring-cloud-kubernetes`
- `spring-cloud-gateway`

## 2. How this Works

The primary mode of deployment is on a local Kubernetes cluster, though each service can be run standalone or in Docker.

The purpose is to show the many utilities of Spring in Kubernetes with Cassandra as the backing storage tier.

The business domain is an inventory / ecommerce application.

## 3. Setup and Running

### 3.a - Prerequisites
The prerequisites required for this application to run
* Docker
* Kubernetes
* JDK 11+
* Maven

### 3.b - Setup
Clone the current repository
```
git clone https://github.com/DataStax-Examples/spring-k8s-cassandra-microservices.git
```

Start minikube
```
# use docker as the virtualization driver
minikube start --driver=docker --extra-config=apiserver.authorization-mode=RBAC,Node

# tell minikube to use local docker registry
eval `minikube docker-env`
```

Build the services
```
# from the spring-k8s-cassandra-microservices directory
mvn package
```

Build the docker images
```
cd microservice-spring-boot; docker build -t <your-docker-username>/spring-boot-service:1.0.0-SNAPSHOT .
cd microservice-spring-data; docker build -t <your-docker-username>/spring-data-service:1.0.0-SNAPSHOT .
cd gateway-service; docker build -t <your-docker-username>/gateway-service:1.0.0-SNAPSHOT .
```

Alter deployment.yml files with your docker username
```
# replace image name in deploy/spring-boot/spring-boot-deployment.yml
# replace image name in deploy/spring-data/spring-data-deployment.yml
# replace image name in deploy/gateway/gateway-deployment.yml
```

Create namespaces
```
kubectl create ns cass-operator
kubectl create ns spring-boot-service
kubectl create ns spring-data-service
kubectl create ns gateway-service
```

### 3.c - Setup Cassandra Operator
Start the Cassandra operator
```
# create the storage class for the database
kubectl -n cass-operator apply -f deploy/storage-class.yml

# apply the operator manifest
kubectl -n cass-operator apply -f https://raw.githubusercontent.com/DataStax-Academy/kubernetes-workshop-online/master/1-cassandra/11-install-cass-operator-v1.1.yaml

# start a single C* 4.0 node
kubectl -n cass-operator apply -f deploy/cassandra-4.0.0-1node.yml
```

Create the Kubernetes Secrets for database username and password
```
# get the username and password from the secret
kubectl -n cass-operator get secret cluster1-superuser -o yaml

# decode the username and password from the secret
echo <username> | base64 -D && echo ""
echo <password> | base64 -D && echo ""

# create k8s secrets for the services (skip cmd for Spring Boot service if using Astra)
kubectl -n spring-boot-service create secret generic db-secret --from-literal=username=<db-username> --from-literal=password=<db-password>
kubectl -n spring-data-service create secret generic db-secret --from-literal=username=<db-username> --from-literal=password=<db-password>
```

### 3.d - (Optional) Use DataStax Astra for Spring Boot Service

Create a free tier database in [DataStax Astra](https://astra.datastax.com/) with keyspace name `betterbotz`

Download the secure connect bundle from the Astra UI ([docs](https://docs.datastax.com/en/astra/aws/doc/dscloud/astra/dscloudObtainingCredentials.html))

Create secrets for the Astra username/password and secure connect bundle
```
kubectl -n spring-boot-service create secret generic db-secret --from-literal=username=<astra-db-user> --from-literal=password=<astra-db-pass>
kubectl -n spring-boot-service create secret generic astracreds --from-file=secure-connect-bundle=<path-to-secure-connect-bundle>
```

Change [ConfigMap](deploy/spring-boot/spring-boot-service-configmap.yml) to use secure connect bundle
```
apiVersion: v1
kind: ConfigMap
metadata:
  name: spring-boot-service
data:
  application.yml: |-
    astra.secure-connect-bundle: /app/astra/creds
```

Uncomment lines in [Deployment.yml](spring-boot-deployment.yml)
```
volumes:
  - name: astravol
    secret:
      secretName: astracreds
      items:
        - key: secure-connect-bundle
          path: creds
...
volumeMounts:
  - name: astravol
    mountPath: "/app/astra"
    readOnly: true
```

### Running
Start the services
```
# from the spring-k8s-cassandra-microservices directory
kubectl -n spring-boot-service apply -f deploy/spring-boot
kubectl -n spring-data-service apply -f deploy/spring-data
kubectl -n gateway-service apply -f deploy/gateway-service
```

Expose the Gateway endpoint
```
# get the gateway-service pod
kubectl -n gateway-service get pods

# forward the port
kubectl -n gateway-service port-forward <gateway-service-pod> 8080:8080
```

Optionally expose the Spring Boot service endpoints (useful for testing)
```
# get the spring-boot-service pod
kubectl -n spring-boot-service get pods

# forward the port
kubectl -n spring-boot-service port-forward <spring-boot-pod> 8083:8083
```

Optionally expose the Spring Data service endpoints (useful for testing)
```
# get the spring-data-service pod
kubectl -n spring-data-service get pods

# forward the port
kubectl -n spring-data-service port-forward <spring-data-pod> 8081:8081
```

#### Gateway Service endpoints

The Spring Cloud Gateway is running on port 8080 and forwards requests
to the Spring Boot and Spring Data endpoints below. To test that this is
working, you can replace the URLs below with `localhost:8080` with the same
curl commands.

#### Spring Boot service endpoints

Explore the endpoints with Swagger (only works if endpoints exposed above): http://localhost:8083/swagger-ui.html

![Swagger Spring Boot](https://github.com/DataStax-Examples/spring-k8s-cassandra-microservices/blob/master/doc/pics/swagger-spring-boot-service.png?raw=true)

Add products
```
curl -X POST -H "Content-Type: application/json" -d '{"name": "mobile", "id":"123e4567-e89b-12d3-a456-556642440000", "description":"iPhone", "price":"500.00"}' http://localhost:8083/api/products/add
curl -X POST -H "Content-Type: application/json" -d '{"name": "mobile", "id":"123e4567-e89b-12d3-a456-556642440001", "description":"Android", "price":"600.00"}' http://localhost:8083/api/products/add
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

#### Spring Data service endpoints
Add orders
```
curl -H "Content-Type: application/json" -d '{"key": {"orderId":"123e4567-e89b-12d3-a456-556642440000", "productId":"123e4567-e89b-12d3-a456-556642440000"}, "productName":"iPhone", "productPrice":"500.00", "productQuantity":1, "addedToOrderTimestamp": "2020-04-12T11:21:59.001+0000"}' http://localhost:8081/api/orders/add
curl -H "Content-Type: application/json" -d '{"key": {"orderId":"123e4567-e89b-12d3-a456-556642440000", "productId":"123e4567-e89b-12d3-a456-556642440001"}, "productName":"Android", "productPrice":"600.00", "productQuantity":1, "addedToOrderTimestamp": "2020-04-12T11:22:59.001+0000"}' http://localhost:8081/api/orders/add
```
Get orders with order_id = 123e4567-e89b-12d3-a456-556642440000
```
curl http://localhost:8081/api/orders/search/order-by-id?orderId=123e4567-e89b-12d3-a456-556642440000
```
Get order with order_id = 123e4567-e89b-12d3-a456-556642440000 and product_id = 123e4567-e89b-12d3-a456-556642440000
```
curl "http://localhost:8081/api/orders/search/order-by-product-id?orderId=123e4567-e89b-12d3-a456-556642440000&productId=123e4567-e89b-12d3-a456-556642440000"
```
Get only the product name and price of order_id = 123e4567-e89b-12d3-a456-556642440000
```
curl http://localhost:8081/api/orders/search/name-and-price-only?orderId=123e4567-e89b-12d3-a456-556642440000
```
Shows how to use a projection with Spring Data REST
```
curl "http://localhost:8081/api/orders/search/name-and-price-only?orderId=123e4567-e89b-12d3-a456-556642440000&projection=product-name-and-price"
```

Delete order with order_id = 123e4567-e89b-12d3-a456-556642440000 and product_id = 123e4567-e89b-12d3-a456-556642440000
```
curl -X DELETE "http://localhost:8081/api/orders/delete/product-from-order?orderId=123e4567-e89b-12d3-a456-556642440000&productId=123e4567-e89b-12d3-a456-556642440000"
```

Delete order with order_id = 123e4567-e89b-12d3-a456-556642440000
```
curl -X DELETE "http://localhost:8081/api/orders/delete/order?orderId=123e4567-e89b-12d3-a456-556642440000"```
```
