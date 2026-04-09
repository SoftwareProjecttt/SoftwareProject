package com.appointmentsystem.persistence.inmemory;

import com.appointmentsystem.domain.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryAppointmentRepositoryTest {

    // Helper method to create TimeSlot
    private TimeSlot createTimeSlot() {
        return new TimeSlot(
                "TS1",
                LocalDate.now().plusDays(1),
                LocalTime.of(9, 0),
                LocalTime.of(10, 0),
                3,
                1
        );
    }

    // =========================
    // Test findAll initially empty
    // =========================
    @Test
    void testFindAllInitiallyEmpty() {
        InMemoryAppointmentRepository repo = new InMemoryAppointmentRepository();

        List<Appointment> appointments = repo.findAll();

        assertNotNull(appointments);
        assertTrue(appointments.isEmpty());
    }

    // =========================
    // Test save + findAll
    // =========================
    @Test
    void testSaveAppointment() {
        InMemoryAppointmentRepository repo = new InMemoryAppointmentRepository();

        Appointment appointment = new Appointment(
                "A1",
                "Ahmad",
                createTimeSlot(),
                2,
                AppointmentStatus.CONFIRMED
        );

        repo.save(appointment);

        List<Appointment> appointments = repo.findAll();

        assertEquals(1, appointments.size());
    }

    // =========================
    // Test findById exists
    // =========================
    @Test
    void testFindByIdExists() {
        InMemoryAppointmentRepository repo = new InMemoryAppointmentRepository();

        Appointment appointment = new Appointment(
                "A1",
                "Ahmad",
                createTimeSlot(),
                2,
                AppointmentStatus.CONFIRMED
        );

        repo.save(appointment);

        Optional<Appointment> found = repo.findById("A1");

        assertTrue(found.isPresent());
        assertEquals("A1", found.get().getId());
    }

    // =========================
    // Test findById not found
    // =========================
    @Test
    void testFindByIdNotFound() {
        InMemoryAppointmentRepository repo = new InMemoryAppointmentRepository();

        Optional<Appointment> found = repo.findById("UNKNOWN");

        assertFalse(found.isPresent());
    }

    // =========================
    // Test findById case insensitive
    // =========================
    @Test
    void testFindByIdCaseInsensitive() {
        InMemoryAppointmentRepository repo = new InMemoryAppointmentRepository();

        Appointment appointment = new Appointment(
                "A1",
                "Ahmad",
                createTimeSlot(),
                2,
                AppointmentStatus.CONFIRMED
        );

        repo.save(appointment);

        Optional<Appointment> found = repo.findById("a1");

        assertTrue(found.isPresent());
        assertEquals("A1", found.get().getId());
    }
}