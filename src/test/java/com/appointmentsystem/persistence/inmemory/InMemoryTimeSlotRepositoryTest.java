package com.appointmentsystem.persistence.inmemory;

import com.appointmentsystem.domain.TimeSlot;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTimeSlotRepositoryTest {

    // =========================
    // Test findAll()
    // =========================
    @Test
    void testFindAll() {
        InMemoryTimeSlotRepository repo = new InMemoryTimeSlotRepository();

        List<TimeSlot> slots = repo.findAll();

        assertNotNull(slots);
        assertEquals(4, slots.size()); // seeded data
    }

    // =========================
    // Test findById (existing)
    // =========================
    @Test
    void testFindByIdExists() {
        InMemoryTimeSlotRepository repo = new InMemoryTimeSlotRepository();

        Optional<TimeSlot> slot = repo.findById("TS1");

        assertTrue(slot.isPresent());
        assertEquals("TS1", slot.get().getId());
    }

    // =========================
    // Test findById (case insensitive)
    // =========================
    @Test
    void testFindByIdCaseInsensitive() {
        InMemoryTimeSlotRepository repo = new InMemoryTimeSlotRepository();

        Optional<TimeSlot> slot = repo.findById("ts1");

        assertTrue(slot.isPresent());
        assertEquals("TS1", slot.get().getId());
    }

    // =========================
    // Test findById (not found)
    // =========================
    @Test
    void testFindByIdNotFound() {
        InMemoryTimeSlotRepository repo = new InMemoryTimeSlotRepository();

        Optional<TimeSlot> slot = repo.findById("UNKNOWN");

        assertFalse(slot.isPresent());
    }
}