package com.appointmentsystem.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class TimeSlotTest {

    @Test
    void shouldCalculateRemainingCapacity() {
        TimeSlot slot = new TimeSlot("1",
                LocalDate.now(),
                LocalTime.of(10,0),
                LocalTime.of(11,0),
                5,2);

        assertEquals(3, slot.getRemainingCapacity());
    }

    @Test
    void shouldDetectAvailability() {
        TimeSlot slot = new TimeSlot("1",
                LocalDate.now(),
                LocalTime.of(10,0),
                LocalTime.of(11,0),
                5,5);

        assertFalse(slot.isAvailable());
    }
}