# Customer Service

Manages customer entities and provides read-through access to product data via inter-service communication.

## Service Info
- **App name**: `Customer-service`
- **Port**: `8086`
- **DB**: MySQL (`jdbc:mysql://localhost:3306/JewelShopApp`)
- **Discovery**: Eureka at `http://localhost:8761/eureka/`

## Endpoints (base: `/api/customers`)
- `POST /addCustomer` — Create customer
- `GET /{customerId}` — Get customer by ID
- `PUT /{customerId}` — Update customer
- `DELETE /{customerId}` — Delete customer
- `GET /product/{productId}` — View a product by ID (via Product service)
- `GET /products` — View all products (via Product service)
- `GET /products/vendor/{vendorId}` — View products by vendor (via Product service)

## Prerequisites
- Java 17+, Maven 3.6+
- MySQL running with database `JewelShopApp`
- Eureka server on port 8761
- Product service available for product-related endpoints

## Run
```bash
mvn spring-boot:run
```

## Configuration
See `src/main/resources/application.properties` for:
- `server.port=8086`
- JDBC URL and credentials
- `spring.jpa.*`
- `eureka.client.*`

## Notes
- Sends downstream calls to Product service for product lookups.
- Ensure Product service is registered in Eureka to resolve service IDs via gateway if used. 