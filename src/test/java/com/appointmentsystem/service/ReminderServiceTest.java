package com.appointmentsystem.service;

import com.appointmentsystem.ReminderService;
import com.appointmentsystem.domain.Appointment;
import com.appointmentsystem.domain.AppointmentStatus;
import com.appointmentsystem.domain.AppointmentType;
import com.appointmentsystem.domain.TimeSlot;
import com.appointmentsystem.persistence.AppointmentRepository;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for ReminderService.
 *
 * @author Mohammad
 * @version 1.0
 */
class ReminderServiceTest {

    @Test
    void sendUpcomingReminders_sendsReminderForAppointmentWithin24Hours() {
        AppointmentRepository appointmentRepository = mock(AppointmentRepository.class);
        TestNotificationService notificationService = new TestNotificationService();

        Clock fixedClock = Clock.fixed(
                Instant.parse("2026-04-10T08:00:00Z"),
                ZoneId.of("UTC")
        );

        TimeSlot timeSlot = new TimeSlot(
                "TS1",
                LocalDate.of(2026, 4, 10),
                LocalTime.of(20, 0),
                LocalTime.of(21, 0),
                3,
                0
        );

        Appointment appointment = new Appointment(
                "A1",
                "Sara",
                timeSlot,
                1,
                AppointmentStatus.CONFIRMED,
                AppointmentType.INDIVIDUAL
        );

        when(appointmentRepository.findAll()).thenReturn(List.of(appointment));

        ReminderService reminderService = new ReminderService(
                appointmentRepository,
                notificationService,
                fixedClock
        );

        List<String> messages = reminderService.sendUpcomingReminders();

        assertEquals(1, messages.size());
        assertEquals(1, notificationService.getMessages().size());
    }

    @Test
    void sendUpcomingReminders_doesNotSendForCancelledAppointment() {
        AppointmentRepository appointmentRepository = mock(AppointmentRepository.class);
        TestNotificationService notificationService = new TestNotificationService();

        Clock fixedClock = Clock.fixed(
                Instant.parse("2026-04-10T08:00:00Z"),
                ZoneId.of("UTC")
        );

        TimeSlot timeSlot = new TimeSlot(
                "TS2",
                LocalDate.of(2026, 4, 10),
                LocalTime.of(20, 0),
                LocalTime.of(21, 0),
                3,
                0
        );

        Appointment appointment = new Appointment(
                "A2",
                "Omar",
                timeSlot,
                1,
                AppointmentStatus.CANCELLED,
                AppointmentType.INDIVIDUAL
        );

        when(appointmentRepository.findAll()).thenReturn(List.of(appointment));

        ReminderService reminderService = new ReminderService(
                appointmentRepository,
                notificationService,
                fixedClock
        );

        List<String> messages = reminderService.sendUpcomingReminders();

        assertEquals(0, messages.size());
        assertEquals(0, notificationService.getMessages().size());
    }
}
