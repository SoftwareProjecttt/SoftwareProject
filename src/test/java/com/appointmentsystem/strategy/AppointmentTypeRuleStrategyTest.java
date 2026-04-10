package com.appointmentsystem.strategy;

import com.appointmentsystem.domain.Appointment;
import com.appointmentsystem.domain.AppointmentStatus;
import com.appointmentsystem.domain.AppointmentType;
import com.appointmentsystem.domain.TimeSlot;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for AppointmentTypeRuleStrategy.
 *
 * @author Mohammad
 * @version 1.0
 */
class AppointmentTypeRuleStrategyTest {

    @Test
    void isValid_returnsTrueForIndividualWithOneParticipant() {
        Appointment appointment = new Appointment(
                "A1",
                "Ahmad",
                createTimeSlot(LocalTime.of(10, 0), LocalTime.of(11, 0)),
                1,
                AppointmentStatus.CONFIRMED,
                AppointmentType.INDIVIDUAL
        );

        AppointmentTypeRuleStrategy strategy = new AppointmentTypeRuleStrategy();

        assertTrue(strategy.isValid(appointment));
    }

    @Test
    void isValid_returnsFalseForIndividualWithMoreThanOneParticipant() {
        Appointment appointment = new Appointment(
                "A2",
                "Sara",
                createTimeSlot(LocalTime.of(10, 0), LocalTime.of(11, 0)),
                2,
                AppointmentStatus.CONFIRMED,
                AppointmentType.INDIVIDUAL
        );

        AppointmentTypeRuleStrategy strategy = new AppointmentTypeRuleStrategy();

        assertFalse(strategy.isValid(appointment));
    }

    @Test
    void isValid_returnsTrueForGroupWithTwoParticipants() {
        Appointment appointment = new Appointment(
                "A3",
                "Omar",
                createTimeSlot(LocalTime.of(10, 0), LocalTime.of(11, 0)),
                2,
                AppointmentStatus.CONFIRMED,
                AppointmentType.GROUP
        );

        AppointmentTypeRuleStrategy strategy = new AppointmentTypeRuleStrategy();

        assertTrue(strategy.isValid(appointment));
    }

    @Test
    void isValid_returnsFalseForGroupWithOneParticipant() {
        Appointment appointment = new Appointment(
                "A4",
                "Lama",
                createTimeSlot(LocalTime.of(10, 0), LocalTime.of(11, 0)),
                1,
                AppointmentStatus.CONFIRMED,
                AppointmentType.GROUP
        );

        AppointmentTypeRuleStrategy strategy = new AppointmentTypeRuleStrategy();

        assertFalse(strategy.isValid(appointment));
    }

    @Test
    void isValid_returnsTrueForUrgentWithinThirtyMinutes() {
        Appointment appointment = new Appointment(
                "A5",
                "Mona",
                createTimeSlot(LocalTime.of(10, 0), LocalTime.of(10, 30)),
                1,
                AppointmentStatus.CONFIRMED,
                AppointmentType.URGENT
        );

        AppointmentTypeRuleStrategy strategy = new AppointmentTypeRuleStrategy();

        assertTrue(strategy.isValid(appointment));
    }

    @Test
    void isValid_returnsFalseForUrgentOverThirtyMinutes() {
        Appointment appointment = new Appointment(
                "A6",
                "Nour",
                createTimeSlot(LocalTime.of(10, 0), LocalTime.of(11, 0)),
                1,
                AppointmentStatus.CONFIRMED,
                AppointmentType.URGENT
        );

        AppointmentTypeRuleStrategy strategy = new AppointmentTypeRuleStrategy();

        assertFalse(strategy.isValid(appointment));
    }

    @Test
    void isValid_returnsTrueForVirtualAppointment() {
        Appointment appointment = new Appointment(
                "A7",
                "Rami",
                createTimeSlot(LocalTime.of(10, 0), LocalTime.of(11, 0)),
                1,
                AppointmentStatus.CONFIRMED,
                AppointmentType.VIRTUAL
        );

        AppointmentTypeRuleStrategy strategy = new AppointmentTypeRuleStrategy();

        assertTrue(strategy.isValid(appointment));
    }

    private TimeSlot createTimeSlot(LocalTime start, LocalTime end) {
        return new TimeSlot(
                "TS1",
                LocalDate.now().plusDays(1),
                start,
                end,
                5,
                0
        );
    }
}
