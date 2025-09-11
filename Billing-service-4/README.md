# Billing Service - JewelShop Application

A comprehensive microservice for managing bills, payments, and financial transactions in the JewelShop jewellery application.

## Overview

The Billing Service is responsible for:
- Creating and managing bills/invoices for jewellery purchases
- Processing payments with jewellery-specific business rules
- Tracking payment status
- Managing bill items with automatic pricing calculations
- Integration with Customer and Product services
- Jewellery-specific tax calculations (GST: Gold 3%, Others 5%)
- Volume and material-based discount calculations

## Architecture

This service follows the microservices architecture pattern and integrates with:
- **Customer Service**: For customer information validation
- **Product Service**: For jewellery product details and pricing
- **Eureka Server**: For service discovery
- **MySQL Database**: For data persistence

## Technology Stack

- **Java 17**
- **Spring Boot 3.4.2**
- **Spring Cloud 2024.0.0**
- **Spring Data JPA**
- **MySQL Database**
- **OpenFeign Client**
- **Lombok**

## Features

### Bill Management
- Create new bills with jewellery items
- Automatic calculation of totals (subtotal, GST, discounts, total)
- Bill status tracking (PENDING, PAID, CANCELLED)
- Bill number generation

### Payment Processing
- Process payments for bills
- Multiple payment methods support
- Transaction ID generation
- Payment status tracking
- Refund processing

### Jewellery-Specific Business Logic
- **Automatic Pricing**: Calculates prices based on material and weight
- **GST Calculation**: Gold (3%), Other materials (5%)
- **Volume Discounts**: 5% for 3+ items, 10% for 5+ items
- **Material Discounts**: Additional 2% for platinum/diamond items

### Advanced Queries
- Find bills by customer, vendor, or status
- Date range filtering
- Payment history tracking

## API Endpoints

### Bill Management
- `POST /api/bills/create` - Create a new bill
- `GET /api/bills/{billId}` - Get bill by ID
- `GET /api/bills/number/{billNumber}` - Get bill by number
- `GET /api/bills/customer/{customerId}` - Get bills by customer
- `GET /api/bills/vendor/{vendorId}` - Get bills by vendor
- `GET /api/bills/status/{status}` - Get bills by status
- `PUT /api/bills/{billId}/status` - Update bill status
- `DELETE /api/bills/{billId}` - Delete bill

### Payment Management
- `POST /api/payments/process` - Process payment
- `GET /api/payments/{paymentId}` - Get payment by ID
- `GET /api/payments/bill/{billId}` - Get payments by bill
- `GET /api/payments/customer/{customerId}` - Get payments by customer
- `GET /api/payments/status/{status}` - Get payments by status
- `POST /api/payments/{paymentId}/refund` - Process refund

### Health Check
- `GET /api/bills/health` - Bill service health
- `GET /api/payments/health` - Payment service health

## Jewellery Pricing Logic

### Material-Based Pricing (per gram)
- **Gold**: ₹5,000
- **Silver**: ₹80
- **Platinum**: ₹3,500
- **Diamond**: ₹100,000
- **Ruby**: ₹15,000
- **Emerald**: ₹12,000
- **Pearl**: ₹2,000
- **Other Materials**: ₹100

### Tax Structure
- **Gold Jewellery**: 3% GST
- **Other Jewellery**: 5% GST

### Discount Structure
- **Volume Discounts**:
  - 3+ items: 5% discount
  - 5+ items: 10% discount
- **Material Discounts**:
  - Platinum/Diamond: Additional 2% discount

## Database Schema

### Tables
1. **bills** - Main bill information
2. **bill_items** - Individual jewellery items in bills
3. **payments** - Payment transaction records

### Key Fields
- **Bill**: billId, customerId, vendorId, billNumber, status, totals
- **BillItem**: productId, quantity, unitPrice, totalPrice, material, weight
- **Payment**: billId, customerId, amount, status, transactionId

## Configuration

### Application Properties
- **Port**: 8089
- **Database**: MySQL (JewelShopApp)
- **Eureka**: http://localhost:8761/eureka/
- **Service Name**: Billing-service

### Environment Variables
- Database credentials
- Service URLs
- Feature flags

## Running the Service

### Prerequisites
- Java 17+
- Maven 3.6+
- MySQL 8.0+
- Eureka Server running
- Customer Service running
- Product Service running

### Steps
1. Clone the repository
2. Configure database connection
3. Start Eureka Server
4. Start Customer Service
5. Start Product Service
6. Run: `mvn spring-boot:run`

### Docker (Optional)
```bash
docker build -t billing-service .
docker run -p 8089:8089 billing-service
```

## Integration

### Feign Clients
- **CustomerClient**: Communicates with Customer Service
- **ProductClient**: Communicates with Product Service

### Service Discovery
- Registers with Eureka Server
- Discovers other services dynamically
- Load balancing support

## Error Handling

### Exception Types
- `BillNotFoundException` - Bill not found
- `PaymentNotFoundException` - Payment not found
- `CustomerNotFoundException` - Customer not found
- `ProductNotFoundException` - Product not found
- `InvalidBillDataException` - Invalid bill data
- `InvalidPaymentDataException` - Invalid payment data
- `BillStatusException` - Invalid bill status operations
- `PaymentProcessingException` - Payment processing errors
- `RefundProcessingException` - Refund processing errors

### Error Response Format
```json
{
  "timestamp": "2024-01-01T10:00:00",
  "status": 404,
  "error": "Bill Not Found",
  "message": "Bill not found with ID: 123",
  "path": "/api/bills/123"
}
```

## Monitoring & Logging

- **Logging**: SLF4J with Lombok
- **Health Checks**: Built-in endpoints
- **Metrics**: Spring Boot Actuator ready

## Security

- Input validation
- SQL injection prevention
- Error message sanitization
- Audit logging

## Testing

### Unit Tests
- Service layer testing
- Repository layer testing
- Controller testing

### Integration Tests
- End-to-end API testing
- Database integration testing

## Performance

- Connection pooling
- Lazy loading for relationships
- Efficient query optimization
- Caching ready

## Future Enhancements

- Email notifications for payment confirmations
- PDF generation for bills
- Payment gateway integration
- Advanced reporting and analytics
- Multi-currency support
- Tax calculation engine updates
- Loyalty program integration

## Support

For issues and questions:
- Email: billing@jewelshop.com
- Documentation: API endpoints above
- Logs: Application logs

## License

MIT License - see LICENSE file for details. 