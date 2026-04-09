package com.appointmentsystem.service;

import com.appointmentsystem.ScheduleService;
import com.appointmentsystem.domain.TimeSlot;
import com.appointmentsystem.persistence.TimeSlotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ScheduleServiceTest {

    private TimeSlotRepository timeSlotRepository;
    private ScheduleService scheduleService;

    @BeforeEach
    void setUp() {
        timeSlotRepository = mock(TimeSlotRepository.class);
        scheduleService = new ScheduleService(timeSlotRepository);
    }

    @Test
    void getAvailableSlots_shouldReturnOnlyAvailableSlots() {

        TimeSlot availableSlot = new TimeSlot(
                "1",
                LocalDate.now().plusDays(1),
                LocalTime.of(10, 0),
                LocalTime.of(11, 0),
                5,
                2
        );

        TimeSlot fullSlot = new TimeSlot(
                "2",
                LocalDate.now().plusDays(1),
                LocalTime.of(12, 0),
                LocalTime.of(13, 0),
                2,
                2
        );

        when(timeSlotRepository.findAll())
                .thenReturn(Arrays.asList(availableSlot, fullSlot));

        List<TimeSlot> result = scheduleService.getAvailableSlots();

        assertEquals(1, result.size());
        assertTrue(result.contains(availableSlot));
        assertFalse(result.contains(fullSlot));
    }

    @Test
    void getAvailableSlots_shouldReturnEmpty_whenAllFull() {

        TimeSlot fullSlot = new TimeSlot(
                "1",
                LocalDate.now().plusDays(1),
                LocalTime.of(10, 0),
                LocalTime.of(11, 0),
                2,
                2
        );

        when(timeSlotRepository.findAll())
                .thenReturn(List.of(fullSlot));

        List<TimeSlot> result = scheduleService.getAvailableSlots();

        assertTrue(result.isEmpty());
    }

    // 🔥 NEW TEST 1
    @Test
    void getAvailableSlots_shouldReturnAll_whenAllAvailable() {

        TimeSlot slot1 = new TimeSlot(
                "1",
                LocalDate.now().plusDays(1),
                LocalTime.of(9, 0),
                LocalTime.of(10, 0),
                5,
                1
        );

        TimeSlot slot2 = new TimeSlot(
                "2",
                LocalDate.now().plusDays(1),
                LocalTime.of(10, 0),
                LocalTime.of(11, 0),
                5,
                0
        );

        when(timeSlotRepository.findAll())
                .thenReturn(List.of(slot1, slot2));

        List<TimeSlot> result = scheduleService.getAvailableSlots();

        assertEquals(2, result.size());
        assertTrue(result.contains(slot1));
        assertTrue(result.contains(slot2));
    }

    // 🔥 NEW TEST 2
    @Test
    void getAvailableSlots_shouldReturnEmpty_whenNoSlotsExist() {

        when(timeSlotRepository.findAll())
                .thenReturn(List.of());

        List<TimeSlot> result = scheduleService.getAvailableSlots();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}