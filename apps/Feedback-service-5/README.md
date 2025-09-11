# Feedback Service

Captures and queries customer feedback about products and experiences.

## Service Info
- **App name**: `Feedback-service`
- **Port**: `8088`
- **DB**: MySQL (`jdbc:mysql://localhost:3306/JewelShopApp`)
- **Discovery**: Eureka at `http://localhost:8761/eureka/`

## Endpoints (base: `/api/feedback`)
- `POST /add` — Create feedback
- `GET /customer/{customerId}` — List feedback for a customer
- `GET /getAllfeedbacks` — List all feedback

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
- `server.port=8088`
- JDBC URL and credentials
- `spring.jpa.*`
- `eureka.client.*`

## Notes
- Can be extended to join with customers/products for richer reporting. 