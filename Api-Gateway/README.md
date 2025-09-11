# API Gateway

A Spring Cloud Gateway (MVC) entrypoint that routes external traffic to backend microservices and integrates with Eureka for service discovery.

## Service Info
- **App name**: `Api-Gateway`
- **Port**: `4000`
- **Service discovery**: Eureka at `http://localhost:8761/eureka/`

## Routes
Traffic is routed via service IDs registered in Eureka and path-based predicates. Prefixes are stripped before forwarding.

- Product Service
  - Path: `/products/**`
  - Target: `lb://PRODUCT-SERVICE`
  - Filter: `StripPrefix=1`
- Customer Service
  - Path: `/customers/**`
  - Target: `lb://CUSTOMER-SERVICE`
  - Filter: `StripPrefix=1`
- Vendor Service
  - Path: `/vendors/**`
  - Target: `lb://VENDOR-SERVICE`
  - Filter: `StripPrefix=1`
- Feedback Service
  - Path: `/feedback/**`
  - Target: `lb://FEEDBACK-SERVICE`
  - Filter: `StripPrefix=1`

## Prerequisites
- Java 17+
- Maven 3.6+
- Running Eureka server on port 8761
- Target services registered in Eureka

## Run
```bash
mvn spring-boot:run
```

## Configuration
See `src/main/resources/application.properties` for:
- `server.port=4000`
- `spring.cloud.gateway.mvc.routes[...]`
- `eureka.client.*`

## Health
Use downstream service health endpoints via their own APIs; the gateway primarily proxies requests. 