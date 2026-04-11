package com.appointmentsystem.service;

import com.appointmentsystem.NotificationGateway;
import com.appointmentsystem.NotificationService;
import com.appointmentsystem.domain.Appointment;
import com.appointmentsystem.domain.AppointmentStatus;
import com.appointmentsystem.domain.AppointmentType;
import com.appointmentsystem.domain.TimeSlot;
import com.appointmentsystem.observer.AppointmentEventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for NotificationService.
 */
class NotificationServiceTest {

    private NotificationGateway mockGateway;
    private NotificationService service;

    @BeforeEach
    void setup() {
        mockGateway = mock(NotificationGateway.class);
        service = new NotificationService(mockGateway);
    }

    @Test
    void update_handlesBookedEvent() {
        Appointment appointment = createAppointment();
        service.update(appointment, AppointmentEventType.BOOKED);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockGateway).send(eq("Sara@example.com"), messageCaptor.capture());
        
        assertTrue(messageCaptor.getValue().contains("Appointment booked for Sara"));
        assertTrue(messageCaptor.getValue().contains("A1"));
    }

    @Test
    void update_handlesCancelledEvent() {
        Appointment appointment = createAppointment();
        service.update(appointment, AppointmentEventType.CANCELLED);
        
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockGateway).send(eq("Sara@example.com"), messageCaptor.capture());
        
        assertTrue(messageCaptor.getValue().contains("Appointment cancelled for Sara"));
    }

    @Test
    void update_handlesModifiedEvent() {
        Appointment appointment = createAppointment();
        service.update(appointment, AppointmentEventType.MODIFIED);
        
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockGateway).send(eq("Sara@example.com"), messageCaptor.capture());
        
        assertTrue(messageCaptor.getValue().contains("Appointment modified for Sara new slot on"));
    }



    @Test
    void update_handlesNullAppointmentOrCustomerSafely() {
        Appointment nullCustomerAppointment = new Appointment(
                "A2", null, createAppointment().getTimeSlot(), 1, 
                AppointmentStatus.CONFIRMED, AppointmentType.INDIVIDUAL
        );

        assertDoesNotThrow(() -> service.update(nullCustomerAppointment, AppointmentEventType.BOOKED));
    }

    private Appointment createAppointment() {
        TimeSlot timeSlot = new TimeSlot(
                "TS1",
                LocalDate.now().plusDays(1),
                LocalTime.of(10, 0),
                LocalTime.of(11, 0),
                5,
                0
        );

        return new Appointment(
                "A1",
                "Sara",
                timeSlot,
                1,
                AppointmentStatus.CONFIRMED,
                AppointmentType.INDIVIDUAL
        );
    }
}
