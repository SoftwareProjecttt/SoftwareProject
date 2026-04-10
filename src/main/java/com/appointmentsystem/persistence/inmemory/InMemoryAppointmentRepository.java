package com.appointmentsystem.persistence.inmemory;

import com.appointmentsystem.domain.Appointment;
import com.appointmentsystem.persistence.AppointmentRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * In-memory implementation of the appointment repository.
 *
 * @author Mohammad
 * @version 2.0
 */
public class InMemoryAppointmentRepository implements AppointmentRepository {

    private final List<Appointment> appointments = new ArrayList<>();

    @Override
    public void save(Appointment appointment) {
        for (int i = 0; i < appointments.size(); i++) {
            if (appointments.get(i).getId().equalsIgnoreCase(appointment.getId())) {
                appointments.set(i, appointment);
                return;
            }
        }

        appointments.add(appointment);
    }

    @Override
    public Optional<Appointment> findById(String id) {
        return appointments.stream()
                .filter(appointment -> appointment.getId().equalsIgnoreCase(id))
                .findFirst();
    }

    @Override
    public List<Appointment> findAll() {
        return new ArrayList<>(appointments);
    }
}
