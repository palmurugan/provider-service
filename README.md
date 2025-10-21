# Provider Service

## Overview
The Provider Service is a core microservice responsible for managing service providers, their services, availability configurations, and appointment slots within the Serviq platform.

## Technology Stack

### Backend
- **Java 21**: Core programming language
- **Spring Boot 3.5.6**: Application framework
- **Spring Data JPA**: Database access
- **MapStruct**: Object mapping
- **Lombok**: Boilerplate code reduction
- **TestContainers**: Integration testing
- **Liquibase**: Database migrations

### Database
- **PostgreSQL 15+**: Primary database
- **JSONB**: For flexible data storage

## Features

### Provider Management
- Complete CRUD operations for service providers
- Provider search and filtering
- Pagination and sorting support

### Service Management
- Add/remove services for providers
- Service-specific configurations
- Capacity and pricing management

### Availability Management
- Recurring and one-time availability
- Custom availability windows
- Override capabilities for special schedules

### Slot Management
- Automatic slot generation
- Real-time availability checking
- Booking integration

## API Endpoints

### Provider Endpoints
- `POST /api/v1/providers` - Create a new provider
- `GET /api/v1/providers/{id}` - Get provider details
- `PUT /api/v1/providers/{id}` - Update provider
- `GET /api/v1/providers` - List all providers (paginated)

### Service Endpoints
- `POST /api/v1/providers/{id}/services` - Add a service
- `GET /api/v1/providers/{providerId}/services/{serviceId}` - Get service details
- `PUT /api/v1/providers/{providerId}/services/{serviceId}` - Update service
- `GET /api/v1/providers/{providerId}/services` - List all services

### Availability Endpoints
- `POST /api/v1/availability-configs` - Create availability
- `GET /api/v1/availability-configs/provider/{providerId}` - Get provider availability
- `PUT /api/v1/availability-configs/{id}` - Update availability
- `DELETE /api/v1/availability-configs/{id}` - Remove availability

### Slot Endpoints
- `POST /api/v1/slots` - Create slot
- `POST /api/v1/slots/bulk` - Create multiple slots
- `GET /api/v1/slots/available` - Get available slots
- `POST /api/v1/slots/{slotId}/book` - Book a slot

## Getting Started

### Prerequisites
- Java 21
- Maven 3.8+
- PostgreSQL 15+
- Docker (optional)

### Local Development
1. Clone the repository
2. Configure database in `application.yml`:
   ```yaml
   spring:
     datasource:
       url: jdbc:postgresql://localhost:5432/provider_db
       username: your_username
       password: your_password