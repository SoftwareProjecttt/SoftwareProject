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
 * Unit tests for ParticipantLimitRuleStrategy.
 *
 * @author Mohammad
 * @version 2.0
 */
class ParticipantLimitRuleStrategyTest {

    @Test
    void isValid_returnsTrueWhenParticipantCountWithinCapacity() {
        TimeSlot timeSlot = new TimeSlot(
                "TS1",
                LocalDate.now().plusDays(1),
                LocalTime.of(10, 0),
                LocalTime.of(11, 0),
                5,
                2
        );

        Appointment appointment = new Appointment(
                "A1",
                "Ahmad",
                timeSlot,
                2,
                AppointmentStatus.CONFIRMED,
                AppointmentType.GROUP
        );

        ParticipantLimitRuleStrategy strategy = new ParticipantLimitRuleStrategy();

        assertTrue(strategy.isValid(appointment));
    }

    @Test
    void isValid_returnsFalseWhenParticipantCountExceedsCapacity() {
        TimeSlot timeSlot = new TimeSlot(
                "TS2",
                LocalDate.now().plusDays(1),
                LocalTime.of(10, 0),
                LocalTime.of(11, 0),
                5,
                4
        );

        Appointment appointment = new Appointment(
                "A2",
                "Sara",
                timeSlot,
                2,
                AppointmentStatus.CONFIRMED,
                AppointmentType.GROUP
        );

        ParticipantLimitRuleStrategy strategy = new ParticipantLimitRuleStrategy();

        assertFalse(strategy.isValid(appointment));
    }

    @Test
    void isValid_returnsFalseWhenParticipantCountIsZero() {
        TimeSlot timeSlot = new TimeSlot(
                "TS3",
                LocalDate.now().plusDays(1),
                LocalTime.of(10, 0),
                LocalTime.of(11, 0),
                5,
                0
        );

        Appointment appointment = new Appointment(
                "A3",
                "Omar",
                timeSlot,
                0,
                AppointmentStatus.CONFIRMED,
                AppointmentType.INDIVIDUAL
        );

        ParticipantLimitRuleStrategy strategy = new ParticipantLimitRuleStrategy();

        assertFalse(strategy.isValid(appointment));
    }
}
