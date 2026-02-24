# User Service - Quick Reference

## Architecture Decision

✅ **Keycloak ID as Primary Key**

- `User.id` = Keycloak user ID (from JWT subject)
- No separate `keycloakId` field
- Simpler queries and lookups
- Industry standard approach

## Implemented Components

### 1. Model

- `User` - Using Keycloak ID as primary ID with `lastLoginAt` field

### 2. DTOs

- `UserSyncRequest` - For Gateway to sync users
- `UserUpdateRequest` - For profile updates
- `UserResponse` - API response format
- `AssignRoleDTO` - For role assignment
- `StatusUpdateRequest` - For enable/disable users

### 3. Repository

- `UserRepository` - MongoDB repository with email lookup

### 4. Service Layer

- `UserService` - Interface defining operations
- `UserServiceImpl` - Complete business logic implementation

### 5. Controller

- `UserController` - REST API endpoints

### 6. Exception Handling

- `ResourceNotFoundException` - 404 errors
- `ForbiddenException` - 403 errors
- `ConflictException` - 409 errors
- `BadRequestException` - 400 errors
- `GlobalExceptionHandler` - Centralized error handling

### 7. Configuration

- `SecurityConfig` - Disabled auth (handled by Gateway)
- `MongoConfig` - MongoDB auditing enabled
- `application.yaml` - Production-ready config

## API Endpoints

| Method | Endpoint                   | Description             | Access        |
| ------ | -------------------------- | ----------------------- | ------------- |
| POST   | `/api/users/sync`          | Sync user from Keycloak | Gateway only  |
| GET    | `/api/users/{id}`          | Get user by ID          | Authenticated |
| GET    | `/api/users/email/{email}` | Get user by email       | Authenticated |
| PUT    | `/api/users/{id}`          | Update user profile     | Self or Admin |
| POST   | `/api/users/{id}/roles`    | Assign role             | Admin only    |
| PUT    | `/api/users/{id}/status`   | Enable/disable user     | Admin only    |
| DELETE | `/api/users/{id}`          | Soft delete user        | Admin only    |
| GET    | `/api/users`               | List all users          | Admin only    |

## Request Headers (from Gateway)

```
X-User-Id: keycloak-user-id
X-User-Email: user@example.com
X-User-Roles: ROLE_CUSTOMER,ROLE_ADMIN
X-Gateway-Secret: secret-key (for /sync endpoint)
```

## User Sync Flow

```
1. User logs in via Keycloak → JWT generated
2. Gateway validates JWT
3. Gateway calls POST /api/users/sync
4. UserService checks if user exists (by Keycloak ID)
5. If exists: Update lastLoginAt → Return user
6. If new: Create user with ROLE_CUSTOMER → Return user
```

## Business Rules

### Sync User

- Creates new user with Keycloak ID
- Default role: `ROLE_CUSTOMER`
- Default status: `enabled = true`
- Email must be unique
- Updates `lastLoginAt` on each login

### Update Profile

- Users can update their own profile
- Admins can update any profile
- Can change: name, image
- Cannot change: email, role, status

### Assign Role

- Admin only operation
- Can assign: ADMIN, CUSTOMER, THEATRE_OWNER

### Update Status

- Admin only operation
- Disable users to prevent access

### Delete User

- Admin only operation
- Soft delete (sets enabled = false)
- Users cannot delete themselves

## Testing

### Start MongoDB

```bash
docker run -d -p 27017:27017 --name mongodb mongo:latest
```

### Start Services

```bash
# 1. Config Server (port 8888)
cd configserver
mvn spring-boot:run

# 2. Eureka Server (port 8761)
cd eureka
mvn spring-boot:run

# 3. User Service (port 8081)
cd userservice
mvn spring-boot:run
```

### Test Sync Endpoint

```bash
curl -X POST http://localhost:8081/api/users/sync \
  -H "Content-Type: application/json" \
  -H "X-Gateway-Secret: change-this-in-production" \
  -d '{
    "keycloakId": "test-123",
    "email": "test@gmail.com",
    "name": "Test User",
    "image": "https://example.com/photo.jpg",
    "provider": "GOOGLE",
    "providerId": "google-123"
  }'
```

### Test Get User

```bash
curl http://localhost:8081/api/users/test-123 \
  -H "X-User-Id: test-123" \
  -H "X-User-Roles: ROLE_CUSTOMER"
```

### Test Update User

```bash
curl -X PUT http://localhost:8081/api/users/test-123 \
  -H "Content-Type: application/json" \
  -H "X-User-Id: test-123" \
  -H "X-User-Roles: ROLE_CUSTOMER" \
  -d '{
    "name": "Updated Name",
    "image": "https://example.com/new-photo.jpg"
  }'
```

## Next Steps

1. Build Gateway service with Keycloak integration
2. Configure Keycloak realm and clients
3. Add Gateway filters to inject user context headers
4. Test complete authentication flow
5. Build Movie Service
6. Build Booking Service

## Key Differences from Previous Design

### Before

- Separate `id` (MongoDB) and `keycloakId` fields
- Two unique identifiers to maintain
- More complex lookups

### After

- Single `id` field = Keycloak ID
- Simplified data model
- Direct lookups by JWT subject
- Industry standard pattern
- No comments in code (clean codebase)

## Production Checklist

- [ ] Setup MongoDB replica set
- [ ] Configure environment variables
- [ ] Add API rate limiting
- [ ] Setup distributed tracing
- [ ] Configure logging aggregation
- [ ] Add circuit breakers
- [ ] Implement caching (Redis)
- [ ] Setup monitoring (Prometheus + Grafana)
- [ ] Configure Spring profiles (dev, staging, prod)
- [ ] Add comprehensive tests
