package com.appointmentsystem.strategy;

import com.appointmentsystem.domain.Appointment;
import com.appointmentsystem.domain.AppointmentStatus;
import com.appointmentsystem.domain.TimeSlot;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class DurationRuleStrategyTest {

    @Test
    void shouldAccept_whenDurationEqualsMax() {

        TimeSlot slot = new TimeSlot(
                "1",
                LocalDate.now(),
                LocalTime.of(10, 0),
                LocalTime.of(12, 0), // 120 min
                5,
                0
        );

        Appointment appointment = new Appointment(
                "A1",
                "Karam",
                slot,
                1,
                AppointmentStatus.CONFIRMED
        );

        DurationRuleStrategy rule = new DurationRuleStrategy(120);

        assertTrue(rule.isValid(appointment));
    }

    @Test
    void shouldReject_whenDurationExceedsMax() {

        TimeSlot slot = new TimeSlot(
                "1",
                LocalDate.now(),
                LocalTime.of(10, 0),
                LocalTime.of(12, 1), // 121 min
                5,
                0
        );

        Appointment appointment = new Appointment(
                "A1",
                "Karam",
                slot,
                1,
                AppointmentStatus.CONFIRMED
        );

        DurationRuleStrategy rule = new DurationRuleStrategy(120);

        assertFalse(rule.isValid(appointment));
    }
}