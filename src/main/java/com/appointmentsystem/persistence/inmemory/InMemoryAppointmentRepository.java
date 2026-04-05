package com.appointmentsystem.persistence.inmemory;

import com.appointmentsystem.domain.Appointment;
import com.appointmentsystem.persistence.AppointmentRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * In-memory implementation of appointment repository.
 *
 * @author Mohammad
 * @version 2.0
 */
public class InMemoryAppointmentRepository implements AppointmentRepository {

    /** In-memory list of appointments. */
    private final List<Appointment> appointments = new ArrayList<>();

    /**
     * Returns all stored appointments.
     *
     * @return list of appointments
     */
    @Override
    public List<Appointment> findAll() {
        return new ArrayList<>(appointments);
    }

    /**
     * Finds an appointment by id.
     *
     * @param appointmentId appointment id
     * @return optional appointment
     */
    @Override
    public Optional<Appointment> findById(String appointmentId) {
        return appointments.stream()
                .filter(appointment -> appointment.getId().equalsIgnoreCase(appointmentId))
                .findFirst();
    }

    /**
     * Saves an appointment in memory.
     *
     * @param appointment appointment to save
     */
    @Override
    public void save(Appointment appointment) {
        appointments.add(appointment);
    }
}