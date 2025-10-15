# Provider Service

## Overview
The Provider Service is a core microservice responsible for managing service providers within the Serviq platform. It handles provider registration, profile management, and provider-related operations.

## Technology Stack

### Backend
- **Java 21**: Core programming language
- **Spring Boot 3.5.6**: Application framework
- **Spring Data JPA**: Data access layer
- **PostgreSQL**: Primary database
- **MapStruct**: Object mapping
- **Lombok**: Boilerplate code reduction
- **Spring Validation**: Request validation
- **SpringDoc OpenAPI 3.0**: API documentation
- **Spring Boot Actuator**: Application monitoring

### Build & Dependency Management
- **Gradle**: Build automation
- **Docker**: Containerization
- **JUnit 5**: Unit and integration testing

## System Design

### Architecture
- **RESTful API** design following REST principles
- **Layered Architecture**:
  - Controller: Handles HTTP requests and responses
  - Service: Contains business logic
  - Repository: Data access layer
  - DTO: Data Transfer Objects for API contracts
  - Entity: JPA entities for database mapping

### API Documentation
- **OpenAPI 3.0** documentation available at `/swagger-ui.html`
- **Actuator** endpoints at `/actuator` for monitoring

### Database Schema
- **Providers**: Core provider information
- **Provider Contacts**: Contact details for providers
- **Provider Locations**: Physical locations of providers

## Getting Started

### Prerequisites
- Java 21 or higher
- PostgreSQL 13+
- Gradle 8.0+

### Local Development
1. Clone the repository
2. Configure database connection in `application.yml`
3. Run the application:
   ```bash
   ./gradlew bootRun
   ```
4. Access the API documentation at `http://localhost:8080/swagger-ui.html`

### Building the Application
```bash
./gradlew clean build
```

### Running Tests
```bash
./gradlew test
```

## API Endpoints

### Provider Management
- `POST /api/v1/providers` - Create a new provider
- `GET /api/v1/providers/{id}` - Get provider by ID
- `PUT /api/v1/providers/{id}` - Update provider
- `GET /api/v1/providers` - Get all providers with pagination and filtering
- `DELETE /api/v1/providers/{id}` - Delete a provider

### Provider Contacts
- `POST /api/v1/providers/{providerId}/contacts` - Add contact to provider
- `PUT /api/v1/providers/contacts/{contactId}` - Update provider contact
- `DELETE /api/v1/providers/contacts/{contactId}` - Remove contact from provider

### Provider Locations
- `POST /api/v1/providers/{providerId}/locations` - Add location to provider
- `PUT /api/v1/providers/locations/{locationId}` - Update provider location
- `DELETE /api/v1/providers/locations/{locationId}` - Remove location from provider

## Error Handling
The service implements global exception handling with appropriate HTTP status codes and error messages.

## Monitoring
- Health check: `GET /actuator/health`
- Metrics: `GET /actuator/metrics`
- Environment: `GET /actuator/env`

## Best Practices
- **Input Validation**: All API inputs are validated
- **Logging**: Comprehensive logging using SLF4J
- **API Versioning**: Versioned API endpoints
- **DTO Pattern**: Separation of API contracts from domain models
- **Pagination**: All list endpoints support pagination
- **Filtering & Sorting**: Support for filtering and sorting

## Future Enhancements
- Implement caching with Redis
- Add event-driven architecture with Kafka
- Implement distributed tracing
- Add rate limiting and circuit breaking
- Implement comprehensive audit logging

## Contributing
1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License
This project is licensed under the terms of the [LICENSE](LICENSE) file.Provider Service for on boarding providers for smart booking app
