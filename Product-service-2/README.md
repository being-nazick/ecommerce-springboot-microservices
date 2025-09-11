# Product Service

Manages jewellery products and exposes APIs for listing, retrieval, and vendor-based queries.

## Service Info
- **App name**: `Product-service`
- **Port**: `8085`
- **DB**: MySQL (`jdbc:mysql://localhost:3306/JewelShopApp`)
- **Discovery**: Eureka at `http://localhost:8761/eureka/`

## Endpoints (base: `/api/products`)
- `POST /addProduct` — Create a product
- `PUT /{productId}` — Update a product
- `GET /getAll` — List all products
- `GET /{productId}` — Get product by ID
- `GET /vendor/{vendorId}` — List products by vendor
- `DELETE /{productId}` — Delete a product

## Prerequisites
- Java 17+, Maven 3.6+
- MySQL running with database `JewelShopApp`
- Eureka server on port 8761

## Run
```bash
mvn spring-boot:run
```

## Configuration
See `src/main/resources/application.properties` for:
- `server.port=8085`
- JDBC URL and credentials
- `spring.jpa.*`
- `eureka.client.*`

## Notes
- Designed to be consumed by Customer and Billing services.
- Ensure Vendor service data aligns with `vendorId` references in products. 