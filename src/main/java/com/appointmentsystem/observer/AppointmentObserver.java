package com.appointmentsystem.observer;

import com.appointmentsystem.domain.Appointment;

/**
 * Observer interface for appointment lifecycle events.
 *
 * <p>Any class interested in appointment notifications must implement
 * this interface and register itself with a subject service.</p>
 *
 * @author Mohammad
 * @version 1.0
 */
public interface AppointmentObserver {

    /**
     * Called when an appointment event occurs.
     *
     * @param appointment the affected appointment
     * @param eventType   the type of event
     */
    void update(Appointment appointment, AppointmentEventType eventType);
}
