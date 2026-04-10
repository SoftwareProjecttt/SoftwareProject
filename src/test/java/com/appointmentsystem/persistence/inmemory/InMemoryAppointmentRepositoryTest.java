package com.appointmentsystem.persistence.inmemory;

import com.appointmentsystem.domain.Appointment;
import com.appointmentsystem.domain.AppointmentStatus;
import com.appointmentsystem.domain.AppointmentType;
import com.appointmentsystem.domain.TimeSlot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for InMemoryAppointmentRepository.
 *
 * @author Mohammad
 * @version 2.0
 */
class InMemoryAppointmentRepositoryTest {

    private InMemoryAppointmentRepository repository;
    private Appointment appointment;

    @BeforeEach
    void setUp() {
        repository = new InMemoryAppointmentRepository();

        TimeSlot timeSlot = new TimeSlot(
                "TS1",
                LocalDate.now().plusDays(1),
                LocalTime.of(10, 0),
                LocalTime.of(11, 0),
                5,
                0
        );

        appointment = new Appointment(
                "A1",
                "Ahmad",
                timeSlot,
                1,
                AppointmentStatus.CONFIRMED,
                AppointmentType.INDIVIDUAL
        );
    }

    @Test
    void save_addsAppointment() {
        repository.save(appointment);

        Optional<Appointment> result = repository.findById("A1");
        assertTrue(result.isPresent());
        assertEquals("Ahmad", result.get().getCustomerName());
    }

    @Test
    void findById_returnsEmptyWhenNotFound() {
        Optional<Appointment> result = repository.findById("UNKNOWN");
        assertFalse(result.isPresent());
    }

    @Test
    void findAll_returnsAllAppointments() {
        TimeSlot secondSlot = new TimeSlot(
                "TS2",
                LocalDate.now().plusDays(2),
                LocalTime.of(12, 0),
                LocalTime.of(13, 0),
                5,
                0
        );

        Appointment secondAppointment = new Appointment(
                "A2",
                "Sara",
                secondSlot,
                2,
                AppointmentStatus.CONFIRMED,
                AppointmentType.GROUP
        );

        repository.save(appointment);
        repository.save(secondAppointment);

        List<Appointment> all = repository.findAll();

        assertEquals(2, all.size());
    }

    @Test
    void save_updatesExistingAppointmentWithSameId() {
        repository.save(appointment);

        TimeSlot updatedSlot = new TimeSlot(
                "TS3",
                LocalDate.now().plusDays(3),
                LocalTime.of(14, 0),
                LocalTime.of(15, 0),
                5,
                0
        );

        Appointment updatedAppointment = new Appointment(
                "A1",
                "Ahmad Updated",
                updatedSlot,
                1,
                AppointmentStatus.CONFIRMED,
                AppointmentType.INDIVIDUAL
        );

        repository.save(updatedAppointment);

        Optional<Appointment> result = repository.findById("A1");
        assertTrue(result.isPresent());
        assertEquals("Ahmad Updated", result.get().getCustomerName());
    }

    @Test
    void findAll_returnsEmptyListInitially() {
        List<Appointment> all = repository.findAll();
        assertTrue(all.isEmpty());
    }
}
