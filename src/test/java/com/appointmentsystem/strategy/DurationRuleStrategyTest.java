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
 * Unit tests for DurationRuleStrategy.
 *
 * @author Mohammad
 * @version 2.0
 */
class DurationRuleStrategyTest {

    @Test
    void isValid_returnsTrueWhenDurationWithinLimit() {
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

        DurationRuleStrategy strategy = new DurationRuleStrategy(120);

        assertTrue(strategy.isValid(appointment));
    }

    @Test
    void isValid_returnsFalseWhenDurationExceedsLimit() {
        TimeSlot timeSlot = new TimeSlot(
                "TS2",
                LocalDate.now().plusDays(1),
                LocalTime.of(10, 0),
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

        DurationRuleStrategy strategy = new DurationRuleStrategy(120);

        assertFalse(strategy.isValid(appointment));
    }
}
