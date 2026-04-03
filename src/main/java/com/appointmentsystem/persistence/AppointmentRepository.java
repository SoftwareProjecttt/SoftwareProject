package com.appointmentsystem.persistence;

import com.appointmentsystem.domain.Appointment;

import java.util.List;
import java.util.Optional;

/**
 * Repository for appointment persistence.
 *
 * @author Mohammad
 * @version 2.0
 */
public interface AppointmentRepository {

    /**
     * Returns all appointments.
     *
     * @return list of appointments
     */
    List<Appointment> findAll();

    /**
     * Finds an appointment by id.
     *
     * @param appointmentId appointment id
     * @return optional appointment
     */
    Optional<Appointment> findById(String appointmentId);

    /**
     * Saves an appointment.
     *
     * @param appointment appointment to save
     */
    void save(Appointment appointment);
}