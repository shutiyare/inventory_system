# Inventory System

A Spring Boot application built with Java 25, PostgreSQL, and Docker. This project demonstrates clean code architecture with standard MVC structure, custom exception handling, and modern Java features like Records.

## Features

- ✅ Spring Boot 3.5.7 with Java 25
- ✅ PostgreSQL database with Docker Compose
- ✅ Complete CRUD operations for User entity
- ✅ Clean MVC architecture
- ✅ Custom exception handling
- ✅ Java Records for DTOs
- ✅ Comprehensive logging
- ✅ Input validation
- ✅ Docker support with multi-stage builds

## Prerequisites

- Java 25
- Maven 3.9+
- Docker and Docker Compose

## Project Structure

```
src/main/java/com/shutiye/inventory_system/
├── controller/          # REST controllers
├── service/            # Business logic layer
├── repository/         # Data access layer
├── entity/             # JPA entities
├── dto/                # Data Transfer Objects (Records)
├── exception/          # Custom exceptions and handlers
└── config/             # Configuration classes
```

## Database Configuration

- **Database Name**: inventory_db
- **Username**: shutiye
- **Password**: carfaaye143
- **Port**: 5432

## Running the Application

### Option 1: Using Docker Compose (Recommended)

1. Build and start all services:
```bash
docker-compose up --build
```

2. The application will be available at: `http://localhost:8080`

### Option 2: Local Development

1. Start PostgreSQL using Docker Compose:
```bash
docker-compose up postgres -d
```

2. Run the Spring Boot application:
```bash
./mvnw spring-boot:run
```

## API Endpoints

### User Management

- **POST** `/api/v1/users` - Create a new user
- **GET** `/api/v1/users` - Get all users
- **GET** `/api/v1/users/{id}` - Get user by ID
- **PUT** `/api/v1/users/{id}` - Update user
- **DELETE** `/api/v1/users/{id}` - Delete user

### Example Request (Create User)

```bash
curl -X POST http://localhost:8080/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "email": "john.doe@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "password": "password123",
    "phoneNumber": "+1234567890",
    "active": true
  }'
```

## Building Docker Image

```bash
docker build -t inventory-system:latest .
```

## Technologies Used

- **Spring Boot** 3.5.7
- **Java** 25
- **PostgreSQL** (Latest)
- **Spring Data JPA**
- **Lombok**
- **Docker** & **Docker Compose**
- **Maven**

## Code Quality

- Clean code principles
- Standard MVC architecture
- Comprehensive logging with SLF4J
- Input validation with Jakarta Validation
- Custom exception handling
- Transaction management
- Modern Java features (Records, Pattern Matching, etc.)

## License

This project is for educational purposes.

