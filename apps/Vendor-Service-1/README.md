# Vendor Service

Manages vendor entities that supply jewellery products.

## Service Info
- **App name**: `Vendor-Service`
- **Port**: `8082`
- **DB**: MySQL (`jdbc:mysql://localhost:3306/JewelShopApp`)
- **Discovery**: Eureka at `http://localhost:8761/eureka/`

## Endpoints (base: `/api/vendors`)
- `POST /add` — Create vendor
- `PUT /{vendorId}` — Update vendor
- `GET /{vendorId}` — Get vendor by ID
- `GET /getAll` — List all vendors
- `DELETE /{vendorId}` — Delete vendor

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
- `server.port=8082`
- JDBC URL and credentials
- `spring.jpa.*`
- `eureka.client.*`

## Notes
- Product service references `vendorId` for vendor-based product queries. 