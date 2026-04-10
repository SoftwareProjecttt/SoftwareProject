package com.appointmentsystem.service;

import com.appointmentsystem.NotificationService;
import com.appointmentsystem.domain.Appointment;
import com.appointmentsystem.domain.AppointmentStatus;
import com.appointmentsystem.domain.AppointmentType;
import com.appointmentsystem.domain.TimeSlot;
import com.appointmentsystem.observer.AppointmentEventType;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Unit tests for NotificationService.
 *
 * @author Mohammad
 * @version 1.0
 */
class NotificationServiceTest {

    @Test
    void update_handlesBookedEvent() {
        NotificationService service = new NotificationService();
        Appointment appointment = createAppointment();

        assertDoesNotThrow(() -> service.update(appointment, AppointmentEventType.BOOKED));
    }

    @Test
    void update_handlesCancelledEvent() {
        NotificationService service = new NotificationService();
        Appointment appointment = createAppointment();

        assertDoesNotThrow(() -> service.update(appointment, AppointmentEventType.CANCELLED));
    }

    @Test
    void update_handlesModifiedEvent() {
        NotificationService service = new NotificationService();
        Appointment appointment = createAppointment();

        assertDoesNotThrow(() -> service.update(appointment, AppointmentEventType.MODIFIED));
    }

    @Test
    void send_handlesReminderMessage() {
        NotificationService service = new NotificationService();

        assertDoesNotThrow(() -> service.send("Sara", "Reminder message"));
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
