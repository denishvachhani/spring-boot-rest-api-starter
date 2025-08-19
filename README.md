# Customer Identity Demo

This is a comprehensive Spring Boot application with advanced features and OpenAPI v3 standardization.

## 🌟 Key Features

### Core Functionality
* ✅ **Full CRUD operations** (Create, Read, Update, Delete - soft delete)
* ✅ **DTOs for clear API contracts**
* ✅ **Global Exception Handling** for consistent error responses
* ✅ **Protection Against Multiple Entry Points** (controller + service validation)
* ✅ **Custom Exceptions**
* ✅ **Automatic Auditing** (timestamps)
* ✅ **Comprehensive Unit and Integration Tests**
* ✅ **Pagination and Sorting** for customer retrieval
* ✅ **One-to-Many relationship** (Customer to Address)
* ✅ **Flyway integration** for database migrations
* ✅ **Profile configuration** (Environment specific profiles)

### Authentication & Security
* ✅ **JWT Authentication** (replaced Basic Auth)
* ✅ **Spring Security** with JWT token validation
* ✅ **Stateless REST API** design
* ✅ **Pre-encoded BCrypt passwords** for security
* ✅ **Immutable User objects** to prevent mutations

### API Documentation & Standards
* ✅ **OpenAPI v3.0.1 Specification** - Machine-readable API documentation
* ✅ **Swagger UI Integration** with JWT authentication support
* ✅ **API Versioning Strategy** (`/api/v1/`)
* ✅ **Comprehensive OpenAPI Annotations**
* ✅ **Professional Metadata** (contact, license, servers)
* ✅ **Complete HTTP Status Code Coverage** (200, 201, 400, 401, 403, 404, 422)

### Monitoring & Observability
* ✅ **Spring Boot Actuator** for service monitoring
* ✅ **Health checks and metrics**

### Technology Stack
* ✅ **Java 23** (upgraded from Java 17)
* ✅ **Spring Boot 3.3.1**
* ✅ **PostgreSQL** with HikariCP connection pooling
* ✅ **Maven** build system
* ✅ **Lombok** for boilerplate reduction

## 🚀 API Endpoints

### Authentication Endpoints
```
POST /api/v1/auth/login     - Authenticate user and receive JWT token
POST /api/v1/auth/validate  - Validate JWT token
```

### Customer Management Endpoints
```
GET    /api/v1/customers         - Get all customers (paginated)
POST   /api/v1/customers         - Create new customer
GET    /api/v1/customers/{id}    - Get customer by ID
PUT    /api/v1/customers/{id}    - Update customer
DELETE /api/v1/customers/{id}    - Delete customer (soft delete)
```

### Development Utilities
```
GET /api/v1/customers/populate-timestamps  - Populate timestamps for existing customers
GET /api/v1/customers/generate-dummy-data  - Generate sample data
```

### Documentation & Monitoring
```
GET /swagger-ui/index.html  - Interactive API Documentation
GET /v3/api-docs           - OpenAPI v3.0.1 JSON Specification
GET /actuator/health       - Health check endpoint
GET /actuator              - All actuator endpoints
```

## 🔐 Authentication

The API uses **JWT (JSON Web Token)** authentication:

### Demo Users
| Username | Password  | Role       |
|----------|-----------|------------|
| admin    | admin123  | ROLE_ADMIN, ROLE_USER |
| user     | password  | ROLE_USER  |
| demo     | demo123   | ROLE_USER  |

### Authentication Flow
1. **Login**: POST to `/api/v1/auth/login` with username/password
2. **Receive JWT**: Get token in response
3. **Use Token**: Add `Authorization: Bearer <token>` header to subsequent requests
4. **Validate**: Use `/api/v1/auth/validate` to check token validity

### Example Login Request
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "admin123"}'
```

## 🏗️ Architecture

### Clean Layered Architecture
```
├── Controller Layer    - REST endpoints, validation
├── Service Layer       - Business logic
├── Repository Layer    - Data access
└── Model Layer         - Entities and DTOs
```

### Project Structure
```
src/main/java/com/example/customeridentitydemo/
├── config/             - Configuration classes
│   ├── OpenApiConfig.java     - OpenAPI v3 configuration
│   └── SecurityConfig.java    - JWT security configuration
├── controller/         - REST controllers
│   ├── AuthController.java    - Authentication endpoints
│   └── CustomerController.java - Customer CRUD operations
├── dto/               - Data Transfer Objects
├── exception/         - Custom exceptions and global handler
├── model/            - JPA entities
├── repository/       - Data access layer
├── security/         - JWT utilities and filters
│   ├── JwtUtil.java              - JWT token operations
│   ├── JwtAuthenticationFilter.java - JWT request filter
│   └── CustomUserDetailsService.java - User authentication
└── service/          - Business logic layer
```

## 📚 OpenAPI v3 Compliance

This project follows **OpenAPI v3 standards** for consistent, machine-readable API documentation:

### Features
- ✅ **OpenAPI v3.0.1** specification
- ✅ **Comprehensive annotations** (`@Operation`, `@ApiResponse`, `@Parameter`)
- ✅ **JWT authentication scheme** configuration
- ✅ **Professional metadata** (contact, license, servers)
- ✅ **API versioning** strategy (`/api/v1/`)
- ✅ **Complete HTTP status codes** (200, 201, 400, 401, 403, 404, 422)
- ✅ **Interactive Swagger UI** with JWT support

### Benefits
- 🤖 **Automated tooling** support
- 🔧 **Client SDK generation** capability
- 📖 **Interactive documentation**
- ✅ **API testing** integration
- 📊 **Consistent error responses**

## 🛠️ Development Setup

### Prerequisites
- Java 23
- PostgreSQL 14+
- Maven 3.6+

### Database Setup
```sql
CREATE DATABASE customer_identity_db;
CREATE USER customer_user WITH PASSWORD 'customer_password';
GRANT ALL PRIVILEGES ON DATABASE customer_identity_db TO customer_user;
```

### Run Application
```bash
# Start application
mvn spring-boot:run

# Run tests
mvn test

# Access Swagger UI
open http://localhost:8080/swagger-ui/index.html
```

### Configuration Profiles
- `dev` - Development environment (default)
- `prod` - Production environment
- `test` - Test environment

## 🧪 Testing

### Test Coverage
- ✅ **Unit Tests** - Service layer business logic
- ✅ **Integration Tests** - Full HTTP request/response cycle
- ✅ **Authentication Tests** - JWT token flow validation
- ✅ **Repository Tests** - Database operations

### Run Tests
```bash
# All tests
mvn test

# Specific test class
mvn test -Dtest=AuthControllerIntegrationTest

# With coverage report
mvn jacoco:report
```

## 🎯 Next Steps for Enhancement

### Immediate Improvements
1. **Redis Caching** for customer data
2. **TestContainers** for integration tests
3. **Docker containerization**
4. **Structured logging** with correlation IDs
5. **Rate limiting** implementation

### Advanced Features
1. **Microservices Architecture**
   - API Gateway (Spring Cloud Gateway)
   - Service Discovery (Eureka)
   - Circuit Breaker (Resilience4j)

2. **Enhanced Security**
   - OAuth2 integration
   - Method-level security
   - CORS configuration

3. **Performance Optimization**
   - Database connection pooling optimization
   - Async processing capabilities
   - Advanced caching strategies

4. **Monitoring & Observability**
   - Micrometer metrics
   - Distributed tracing
   - Custom health indicators

## 📋 API Design Standards

### HTTP Status Codes
- `200` - OK (successful GET, PUT)
- `201` - Created (successful POST)
- `204` - No Content (successful DELETE)
- `400` - Bad Request (validation errors)
- `401` - Unauthorized (missing/invalid JWT)
- `403` - Forbidden (valid JWT, insufficient permissions)
- `404` - Not Found (resource doesn't exist)
- `422` - Unprocessable Entity (business validation errors)

### Response Format
```json
{
  "timestamp": "2025-01-15T10:30:00Z",
  "status": 400,
  "error": "Validation Failed",
  "message": "Invalid request data",
  "path": "/api/v1/customers"
}
```

## 🏆 Project Achievements

This project demonstrates:
- ✅ **Enterprise-grade architecture** with clean separation of concerns
- ✅ **Security best practices** with JWT implementation
- ✅ **OpenAPI v3 standardization** for API documentation
- ✅ **Comprehensive testing strategy** with high coverage
- ✅ **Modern Java features** (Java 23)
- ✅ **Production-ready configuration** with profiles
- ✅ **Database migration management** with Flyway
- ✅ **Monitoring and observability** with Actuator

---

**Built with ❤️ using Spring Boot, Java 23, and OpenAPI v3 standards**