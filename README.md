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
Open the project in IntelliJ IDEA or any Java IDE, then run:

```bash
mvn compile
mvn exec:java -Dexec.mainClass="com.appointmentsystem.Main"
