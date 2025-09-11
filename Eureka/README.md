# Eureka Service

Service registry for discovery of microservices in the JewelShop system.

## Service Info
- **App name**: `Eureka-server`
- **Port**: `8761`
- **Self registration**: disabled (server-only)

## Purpose
- Central registry where microservices (Product, Customer, Vendor, Billing, Feedback, Api-Gateway) register and discover each other.

## Run
```bash
mvn spring-boot:run
```
Then open `http://localhost:8761` to view the dashboard.

## Configuration
See `src/main/resources/application.properties`:
- `server.port=8761`
- `eureka.client.registerWithEureka=false`
- `eureka.client.fetchRegistry=false`
- `eureka.server.enableSelfPreservation=false`

## Notes
- Start Eureka before other services so they can register successfully. 