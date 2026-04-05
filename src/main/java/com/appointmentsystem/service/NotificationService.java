package com.appointmentsystem.service;

import com.appointmentsystem.domain.Appointment;
import com.appointmentsystem.observer.AppointmentEventType;
import com.appointmentsystem.observer.AppointmentObserver;

/**
 * Notification service that reacts to appointment lifecycle events.
 *
 * <p>This is a mock implementation that prints messages to the console.
 * In a real system this would send emails, SMS, or push notifications.</p>
 *
 * @author Mohammad
 * @version 1.0
 */
public class NotificationService implements AppointmentObserver {

    /**
     * Receives an appointment event and prints the appropriate notification.
     *
     * @param appointment the affected appointment
     * @param eventType   the type of event
     */
    @Override
    public void update(Appointment appointment, AppointmentEventType eventType) {
        switch (eventType) {

            case BOOKED:
                System.out.println(
                        "[Notification] Appointment booked for "
                        + appointment.getCustomerName()
                        + " on " + appointment.getTimeSlot().getDate()
                        + " at " + appointment.getTimeSlot().getStartTime()
                        + " (ID: " + appointment.getId() + ")"
                );
                break;

            case CANCELLED:
                System.out.println(
                        "[Notification] Appointment cancelled for "
                        + appointment.getCustomerName()
                        + " on " + appointment.getTimeSlot().getDate()
                        + " at " + appointment.getTimeSlot().getStartTime()
                        + " (ID: " + appointment.getId() + ")"
                );
                break;

            case MODIFIED:
                System.out.println(
                        "[Notification] Appointment modified for "
                        + appointment.getCustomerName()
                        + " — new slot on " + appointment.getTimeSlot().getDate()
                        + " at " + appointment.getTimeSlot().getStartTime()
                        + " (ID: " + appointment.getId() + ")"
                );
                break;
        }
    }
}
