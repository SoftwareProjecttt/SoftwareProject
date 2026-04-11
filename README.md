# Appointment Scheduling System

## Team Members
- Karam Salahat
- Ghadeh Qanazeh
- Sara Darawzeh

## Overview
This project is a Java-based Appointment Scheduling System developed for the Software Engineering course.  
The system supports administrator authentication, appointment booking, reservation management, appointment reminders, and multiple appointment types.

The project was built using Java, Maven, JUnit 5, JaCoCo, and Mockito while following a layered architecture and applying design patterns such as Strategy and Observer.

## Features

### Core Features
- Administrator login
- Administrator logout
- View available appointment slots
- Book appointment
- Modify appointment
- Cancel appointment
- Administrator reservation management

### CLI Interface
- Interactive command-line interface (CLI)
- Menu-driven navigation for all system operations
- Input validation and error handling
- Admin-only protected actions

### Email Notifications
- Email notification simulation on:
    - Booking
    - Modification
    - Cancellation
- Implemented using NotificationGateway abstraction
- Designed for easy replacement with real email services
- Mocked in tests using Mockito

### Testing & Coverage
- Unit testing using JUnit 5
- Mocking using Mockito
- Code coverage using JaCoCo
- Achieved over 80% overall test coverage

### Business Rules
- Appointment duration validation
- Participant capacity validation
- Type-specific appointment validation

### Appointment Types
- Urgent
- Follow-up
- Assessment
- Virtual
- In-person
- Individual
- Group

### Notifications
- Appointment booking notifications
- Appointment modification notifications
- Appointment cancellation notifications
- Appointment reminder generation for upcoming appointments

## Technologies Used
- Java 17
- Maven
- JUnit 5
- Mockito
- JaCoCo

## Design Patterns

### Strategy Pattern
Used for booking rule validation:
- `DurationRuleStrategy`
- `ParticipantLimitRuleStrategy`
- `AppointmentTypeRuleStrategy`

### Observer Pattern
Used for appointment event notifications:
- Booking notifications
- Cancellation notifications
- Modification notifications

## Project Structure
- `presentation` : CLI interface
- `service / application` : business logic and coordination
- `domain` : entities and enums
- `persistence` : repository interfaces and in-memory implementations
- `strategy` : booking validation rules
- `observer` : appointment event observers
- `security` : session handling
- `exception` : custom exceptions

## How to Run the Project

1. Compile the project:
   mvn compile

2. Run the application:
   mvn exec:java -Dexec.mainClass="com.appointmentsystem.Main"

3. Follow the CLI menu to interact with the system.