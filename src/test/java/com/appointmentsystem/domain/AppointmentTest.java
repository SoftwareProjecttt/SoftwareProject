package com.appointmentsystem.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class AppointmentTest {

    @Test
    void shouldCancelAppointment() {
        TimeSlot slot = new TimeSlot("1",
                LocalDate.now().plusDays(1),
                LocalTime.of(10,0),
                LocalTime.of(11,0),
                5,0);

        Appointment appointment = new Appointment(
                "1","karam",slot,2,AppointmentStatus.CONFIRMED
        );

        appointment.cancel();

        assertEquals(AppointmentStatus.CANCELLED, appointment.getStatus());
    }

    @Test
    void shouldCheckBelongsToCustomer() {
        TimeSlot slot = new TimeSlot("1",
                LocalDate.now().plusDays(1),
                LocalTime.of(10,0),
                LocalTime.of(11,0),
                5,0);

        Appointment appointment = new Appointment(
                "1","karam",slot,2,AppointmentStatus.CONFIRMED
        );

        assertTrue(appointment.belongsToCustomer("karam"));
        assertFalse(appointment.belongsToCustomer("ahmad"));
    }
}