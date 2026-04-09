package com.appointmentsystem.strategy;

import com.appointmentsystem.domain.Appointment;
import com.appointmentsystem.domain.AppointmentStatus;
import com.appointmentsystem.domain.TimeSlot;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class ParticipantLimitRuleStrategyTest {

    @Test
    void shouldAccept_whenWithinCapacity() {

        TimeSlot slot = new TimeSlot(
                "1",
                LocalDate.now(),
                LocalTime.of(10, 0),
                LocalTime.of(11, 0),
                5,
                2
        );

        Appointment appointment = new Appointment(
                "A1",
                "Karam",
                slot,
                2,
                AppointmentStatus.CONFIRMED
        );

        ParticipantLimitRuleStrategy rule = new ParticipantLimitRuleStrategy();

        assertTrue(rule.isValid(appointment));
    }

    @Test
    void shouldReject_whenExceedsCapacity() {

        TimeSlot slot = new TimeSlot(
                "1",
                LocalDate.now(),
                LocalTime.of(10, 0),
                LocalTime.of(11, 0),
                2,
                1
        );

        Appointment appointment = new Appointment(
                "A1",
                "Karam",
                slot,
                3,
                AppointmentStatus.CONFIRMED
        );

        ParticipantLimitRuleStrategy rule = new ParticipantLimitRuleStrategy();

        assertFalse(rule.isValid(appointment));
    }

    @Test
    void shouldReject_whenZeroParticipants() {

        TimeSlot slot = new TimeSlot(
                "1",
                LocalDate.now(),
                LocalTime.of(10, 0),
                LocalTime.of(11, 0),
                5,
                0
        );

        Appointment appointment = new Appointment(
                "A1",
                "Karam",
                slot,
                0,
                AppointmentStatus.CONFIRMED
        );

        ParticipantLimitRuleStrategy rule = new ParticipantLimitRuleStrategy();

        assertFalse(rule.isValid(appointment));
    }
}