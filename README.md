# Spring Microservices - Sprint Final

## Services and Ports
- Eureka Server (`Eureka`): 8761 (`spring.application.name=Eureka-server`)
- API Gateway (`Api-Gateway`): 4000
- Vendor Service (`Vendor-Service-1`): 8082 (`spring.application.name=Vendor-Service`)
- Product Service (`Product-service-2`): 8085 (`spring.application.name=Product-service`)
- Customer Service (`Customer-service-3`): 8086 (`spring.application.name=Customer-service`)
- Feedback Service (`Feedback-service-5`): 8088 (`spring.application.name=Feedback-service`)

## Prereqs
- JDK 17 installed and on PATH
- MySQL running with database `JewelShopApp`
- Maven Wrapper will download dependencies automatically

## Start Order
1. Eureka Server
```
cd Eureka
./mvnw.cmd spring-boot:run
```
2. Domain Services (each in its own terminal)
```
cd Vendor-Service-1 && ./mvnw.cmd spring-boot:run
cd Product-service-2 && ./mvnw.cmd spring-boot:run
cd Customer-service-3 && ./mvnw.cmd spring-boot:run
cd Feedback-service-5 && ./mvnw.cmd spring-boot:run
```
3. API Gateway
```
cd Api-Gateway
./mvnw.cmd spring-boot:run
```

Verify service registration at `http://localhost:8761`.

## Gateway Routes (prefix → service)
- `/products/**` → `PRODUCT-SERVICE`
- `/customers/**` → `CUSTOMER-SERVICE`
- `/vendors/**` → `VENDOR-SERVICE`
- `/feedback/**` → `FEEDBACK-SERVICE`

Examples (through gateway at `http://localhost:4000`):
- Products: `GET /products/api/products/getAll`, `GET /products/api/products/{id}`
- Vendors: `GET /vendors/api/vendors/getAll`, `GET /vendors/api/vendors/{id}`
- Customers: `GET /customers/api/customers/{id}`
- Feedback: `GET /feedback/api/feedback/customer/{customerId}`

## Notes
- Feign clients resolve services by `spring.application.name` via Eureka
- DB credentials are configured in each service's `application.properties` 
=======
# ecommerce-springboot-microservices
Distributed system architecture using independent services such as API Gateway, Eureka service discovery, Customer, Product, Vendor, and Feedback microservices.
>>>>>>> af48b3359e98f8e384bb83b5734e927a22b1beb4
