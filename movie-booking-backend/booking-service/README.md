# Booking Service

## Overview

The Booking Service is a core microservice in the Movie Booking Platform that handles all booking operations, including creating bookings, managing booking lifecycle, payment integration through event-driven architecture, and booking history.

## Features

- 📝 Create new movie show bookings
- 🔍 Get booking details by ID
- 📜 View booking history for users
- ❌ Cancel bookings
- 💳 Payment integration via Kafka events
- ⏰ Automatic booking expiry (10-minute timeout)
- 🔒 Seat locking integration with Inventory Service
- 🎫 Unique booking reference generation
- 💰 Automatic calculation of taxes and convenience fees

## Technology Stack

- **Framework**: Spring Boot 3.2.5
- **Java Version**: 21
- **Database**: PostgreSQL
- **Authentication**: OAuth2 Resource Server (Keycloak)
- **Message Broker**: Apache Kafka
- **Inter-Service Communication**: OpenFeign
- **Build Tool**: Maven

## Database Schema

### Bookings Table

```sql
CREATE TABLE bookings (
    id VARCHAR(255) PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    show_id VARCHAR(255) NOT NULL,
    movie_id VARCHAR(255) NOT NULL,
    theatre_id VARCHAR(255) NOT NULL,
    screen_id VARCHAR(255) NOT NULL,
    movie_name VARCHAR(500),
    theatre_name VARCHAR(500),
    screen_name VARCHAR(255),
    show_time TIMESTAMP,
    total_amount DECIMAL(10,2) NOT NULL,
    convenience_fee DECIMAL(10,2),
    total_tax DECIMAL(10,2),
    final_amount DECIMAL(10,2),
    status VARCHAR(50) NOT NULL,
    payment_id VARCHAR(255),
    transaction_id VARCHAR(255),
    booking_date TIMESTAMP,
    expiry_time TIMESTAMP,
    booking_reference VARCHAR(50) UNIQUE,
    version BIGINT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
```

### Booking Seats Table

```sql
CREATE TABLE booking_seats (
    id VARCHAR(255) PRIMARY KEY,
    booking_id VARCHAR(255) NOT NULL,
    seat_id VARCHAR(255) NOT NULL,
    seat_number VARCHAR(50),
    row_number INTEGER,
    seat_number_in_row INTEGER,
    seat_type VARCHAR(50),
    price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (booking_id) REFERENCES bookings(id)
);
```

## API Endpoints

### 1. Create Booking

**Endpoint**: `POST /api/bookings`  
**Authentication**: Required (CUSTOMER or ADMIN role)

**Request Body**:

```json
{
  "userId": "user-123",
  "showId": "show-456",
  "seats": [
    {
      "seatId": "seat-789",
      "seatNumber": "A1",
      "rowNumber": 1,
      "seatNumberInRow": 1,
      "seatType": "PREMIUM",
      "price": 350.0
    }
  ],
  "idempotencyKey": "unique-key-123"
}
```

**Response**: `201 Created`

```json
{
  "bookingId": "booking-123",
  "bookingReference": "BK12345678901234",
  "userId": "user-123",
  "showId": "show-456",
  "movieId": "movie-789",
  "movieName": "Inception",
  "theatreName": "PVR Cinemas",
  "screenName": "Screen 1",
  "showTime": "2024-03-15T19:30:00",
  "seats": [...],
  "totalAmount": 350.00,
  "convenienceFee": 7.00,
  "totalTax": 64.26,
  "finalAmount": 421.26,
  "status": "PENDING",
  "bookingDate": "2024-03-15T18:00:00",
  "expiryTime": "2024-03-15T18:10:00",
  "message": "Booking created successfully. Please complete payment within 10 minutes."
}
```

### 2. Get Booking by ID

**Endpoint**: `GET /api/bookings/{bookingId}?userId={userId}`  
**Authentication**: Required

**Response**: `200 OK`

```json
{
  "bookingId": "booking-123",
  "bookingReference": "BK12345678901234",
  "status": "CONFIRMED",
  ...
}
```

### 3. Get User Booking History

**Endpoint**: `GET /api/bookings/user/{userId}`  
**Authentication**: Required

**Response**: `200 OK`

```json
[
  {
    "bookingId": "booking-123",
    "bookingReference": "BK12345678901234",
    "movieName": "Inception",
    "theatreName": "PVR Cinemas",
    "screenName": "Screen 1",
    "showTime": "2024-03-15T19:30:00",
    "numberOfSeats": 2,
    "finalAmount": 842.52,
    "status": "CONFIRMED",
    "bookingDate": "2024-03-15T18:00:00"
  }
]
```

### 4. Cancel Booking

**Endpoint**: `DELETE /api/bookings/{bookingId}?userId={userId}`  
**Authentication**: Required

**Response**: `200 OK`

```json
{
  "bookingId": "booking-123",
  "status": "CANCELLED",
  "message": "Booking cancelled successfully"
}
```

### 5. Health Check

**Endpoint**: `GET /api/bookings/health`  
**Authentication**: Not Required

**Response**: `200 OK`

```text
Booking Service is running
```

## Booking Status Lifecycle

```
PENDING → PAYMENT_INITIATED → CONFIRMED
   ↓            ↓
CANCELLED ← EXPIRED
   ↓
REFUNDED
```

- **PENDING**: Initial state when booking is created
- **PAYMENT_INITIATED**: Payment process started (future implementation)
- **CONFIRMED**: Payment successful, booking confirmed
- **CANCELLED**: User cancelled or payment failed
- **EXPIRED**: Booking expired (10-minute timeout)
- **REFUNDED**: Payment refunded after cancellation

## Kafka Events

### Published Events

#### 1. BookingCreatedEvent

**Topic**: `booking-created-event`

```json
{
  "bookingId": "booking-123",
  "userId": "user-123",
  "showId": "show-456",
  "seatIds": ["seat-789", "seat-790"]
}
```

#### 2. BookingCancelledEvent

**Topic**: `booking-cancelled-event`

```json
{
  "bookingId": "booking-123",
  "userId": "user-123",
  "showId": "show-456",
  "seatIds": ["seat-789", "seat-790"],
  "reason": "User cancelled"
}
```

### Consumed Events

#### 1. PaymentSuccessfulEvent

**Topic**: `payment-successful-event`

```json
{
  "bookingId": "booking-123",
  "userId": "user-123",
  "showId": "show-456",
  "paymentId": "payment-456",
  "transactionId": "txn-789"
}
```

#### 2. PaymentFailedEvent

**Topic**: `payment-failed-event`

```json
{
  "bookingId": "booking-123",
  "userId": "user-123",
  "showId": "show-456",
  "reason": "Insufficient balance"
}
```

## Service Dependencies

### External Services (via Feign Clients)

1. **Movie Service** (port 8082)
   - Get movie details

2. **Show Service** (port 8084)
   - Get show details (time, screen, pricing)

3. **Theatre Service** (port 8083)
   - Get theatre and screen details

4. **Inventory Service** (port 8085)
   - Lock seats before booking
   - Release seats on cancellation/expiry

## Business Logic

### Booking Creation Flow

1. Validate request data
2. Fetch show details from Show Service
3. Fetch movie details from Movie Service
4. Fetch theatre and screen details from Theatre Service
5. Lock seats via Inventory Service (with 10-minute expiry)
6. Create booking entity with PENDING status
7. Calculate totals:
   - Total Amount = Sum of seat prices
   - Convenience Fee = 2% of total amount
   - Total Tax = 18% GST on (total amount + convenience fee)
   - Final Amount = Total + Convenience Fee + Tax
8. Generate unique booking reference
9. Save booking to database
10. Publish BookingCreatedEvent to Kafka
11. Return booking response to user

### Payment Success Flow

1. Consume PaymentSuccessfulEvent from Kafka
2. Update booking status to CONFIRMED
3. Store payment ID and transaction ID
4. Inventory Service confirms seat booking (via its own listener)

### Payment Failure Flow

1. Consume PaymentFailedEvent from Kafka
2. Update booking status to CANCELLED
3. Publish BookingCancelledEvent
4. Inventory Service releases seats (via its own listener)

### Booking Expiry Flow (Scheduler)

1. Run every 1 minute (scheduled task)
2. Find all PENDING bookings with expiry time < current time
3. Update status to EXPIRED
4. Publish BookingCancelledEvent for each expired booking
5. Inventory Service releases seats

## Configuration

### Environment Variables

```properties
DB_USERNAME=postgres
DB_PASSWORD=your_password
MOVIE_SERVICE_URL=http://localhost:8082
SHOW_SERVICE_URL=http://localhost:8084
THEATRE_SERVICE_URL=http://localhost:8083
INVENTORY_SERVICE_URL=http://localhost:8085
```

### Database Setup

```bash
# Create PostgreSQL database
psql -U postgres
CREATE DATABASE "booking-db";
```

### Running the Service

```bash
# Using Maven
./mvnw spring-boot:run

# Using Java
java -jar target/booking-service-0.0.1-SNAPSHOT.jar
```

The service will start on **port 8086**.

## Testing

### Test Scenarios

1. ✅ Create booking with valid seats
2. ✅ Get booking by ID
3. ✅ Get user booking history
4. ✅ Cancel booking before expiry
5. ✅ Handle payment success
6. ✅ Handle payment failure
7. ✅ Automatic expiry of pending bookings
8. ❌ Attempt to book already locked seats
9. ❌ Cancel already cancelled booking
10. ❌ Access booking of different user

## Integration Points

### Synchronous (OpenFeign)

- Movie Service: Get movie details
- Show Service: Get show details
- Theatre Service: Get theatre/screen details
- Inventory Service: Lock seats

### Asynchronous (Kafka)

- Publish: booking-created-event, booking-cancelled-event
- Consume: payment-successful-event, payment-failed-event

## Security

- OAuth2 Resource Server with JWT tokens
- Role-based access control (CUSTOMER, ADMIN)
- Keycloak integration for authentication
- CORS enabled for frontend integration
- Stateless session management

## Monitoring & Observability

- Spring Actuator endpoints: `/actuator/health`, `/actuator/metrics`
- Comprehensive logging with SLF4J
- Swagger UI: `http://localhost:8086/swagger-ui.html`
- OpenAPI Docs: `http://localhost:8086/api-docs`

## Future Enhancements

1. 🔄 Implement circuit breaker with Resilience4j
2. 📧 Email/SMS notifications on booking confirmation
3. 🎟️ Generate PDF e-tickets
4. 💸 Refund processing integration
5. 📊 Booking analytics and reporting
6. 🔍 Advanced search and filtering
7. ⭐ Seat preference recommendations
8. 🎁 Promo code and discount support

## Error Handling

- Custom exception handling with `@RestControllerAdvice`
- Proper HTTP status codes
- Detailed error messages
- Validation error mapping

## Maintainers

- Backend Team - Movie Booking Platform

## License

Proprietary - Internal Use Only
