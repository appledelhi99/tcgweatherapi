# tcgweatherapi
# Weather API Application

## Overview
The **Weather API Application** is a Spring Boot-based application that provides weather details for US cities using their ZIP codes. The application also includes user management features, such as user registration, activation, deactivation, and tracking the history of weather requests. It uses an external weather API for fetching real-time weather data.

---

## Features
1. **User Management**:
   - User registration with email validation.
   - Activate and deactivate user accounts.
   - Retrieve weather request history by user email or ZIP code.

2. **Weather Information**:
   - Fetch current weather details for valid US ZIP codes.
   - Save and track weather requests.

3. **Validation**:
   - ZIP code validation for US cities.
   - Centralized error handling with meaningful error messages.

4. **Database Integration**:
   - H2 in-memory database for saving user and weather request data.

5. **API Documentation**:
   - Swagger UI is integrated for easy API exploration.

---

## Technologies Used
- **Java 17**: Programming language.
- **Spring Boot 3.x**: Framework for building the application.
- **Spring Data JPA**: For database interaction.
- **H2 Database**: In-memory database for testing and development.
- **Springdoc OpenAPI**: For generating API documentation and Swagger UI.
- **Apache Commons Validator**: For ZIP code validation.
- **Mockito & JUnit 5**: For unit testing.

---

## Prerequisites
- Java 17+
- Maven 3.6+
- Internet connection (for fetching weather data).

---

## Setup and Run

### 1. Clone the Repository
```bash
git clone https://github.com/your-repo/weather-api.git
cd weather-api
