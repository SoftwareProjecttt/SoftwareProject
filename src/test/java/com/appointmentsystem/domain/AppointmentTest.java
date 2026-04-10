package com.appointmentsystem.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for Appointment.
 *
 * @author Mohammad
 * @version 2.0
 */
class AppointmentTest {

    @Test
    void cancel_changesStatusToCancelled() {
        TimeSlot timeSlot = new TimeSlot(
                "TS1",
                LocalDate.now().plusDays(1),
                LocalTime.of(10, 0),
                LocalTime.of(11, 0),
                5,
                0
        );

        Appointment appointment = new Appointment(
                "A1",
                "Ahmad",
                timeSlot,
                1,
                AppointmentStatus.CONFIRMED,
                AppointmentType.INDIVIDUAL
        );

        appointment.cancel();

        assertEquals(AppointmentStatus.CANCELLED, appointment.getStatus());
    }

    @Test
    void belongsToCustomer_returnsTrueForMatchingName() {
        TimeSlot timeSlot = new TimeSlot(
                "TS2",
                LocalDate.now().plusDays(1),
                LocalTime.of(12, 0),
                LocalTime.of(13, 0),
                5,
                0
        );

        Appointment appointment = new Appointment(
                "A2",
                "Sara",
                timeSlot,
                1,
                AppointmentStatus.CONFIRMED,
                AppointmentType.INDIVIDUAL
        );

        assertTrue(appointment.belongsToCustomer("Sara"));
    }
}
