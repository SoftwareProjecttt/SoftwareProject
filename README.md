# Appointment Scheduling System

## Overview

This project is a Java-based Appointment Scheduling System developed as part of the Software Engineering course. The system follows a layered architecture and is implemented incrementally using agile principles.

## Implemented Features

### Core Functionality

* Administrator login and logout
* Viewing available appointment slots
* Booking appointments with validation rules
* Modifying and cancelling appointments (user and admin)

### Business Rules

* Appointment duration constraints
* Participant capacity limits

### Design Patterns

* Strategy Pattern for booking rule validation
* Observer Pattern for handling appointment notifications

### Testing

* Unit tests implemented using JUnit 5 and Mockito
* Coverage includes booking logic, validation, and observer behavior

## Project Structure

* Presentation Layer
* Service Layer
* Domain Layer
* Persistence Layer

## Progress Summary

The project has been developed incrementally. Initial implementation focused on core scheduling functionality and rule enforcement. Additional enhancements include notification handling using the Observer pattern and unit testing for core services.

## Technologies Used

* Java
* Maven
* JUnit 5
* Mockito
