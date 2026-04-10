package com.appointmentsystem;

import com.appointmentsystem.domain.Appointment;
import com.appointmentsystem.observer.AppointmentEventType;
import com.appointmentsystem.observer.AppointmentObserver;

/**
 * Notification service that reacts to appointment lifecycle events.
 *
 * @author Mohammad
 * @version 2.0
 */
public class NotificationService implements AppointmentObserver, NotificationGateway {

    @Override
    public void update(Appointment appointment, AppointmentEventType eventType) {
        switch (eventType) {
            case BOOKED:
                System.out.println("[Notification] Appointment booked for "
                        + appointment.getCustomerName()
                        + " on " + appointment.getTimeSlot().getDate()
                        + " at " + appointment.getTimeSlot().getStartTime()
                        + " (ID: " + appointment.getId() + ")");
                break;

            case CANCELLED:
                System.out.println("[Notification] Appointment cancelled for "
                        + appointment.getCustomerName()
                        + " on " + appointment.getTimeSlot().getDate()
                        + " at " + appointment.getTimeSlot().getStartTime()
                        + " (ID: " + appointment.getId() + ")");
                break;

            case MODIFIED:
                System.out.println("[Notification] Appointment modified for "
                        + appointment.getCustomerName()
                        + " new slot on " + appointment.getTimeSlot().getDate()
                        + " at " + appointment.getTimeSlot().getStartTime()
                        + " (ID: " + appointment.getId() + ")");
                break;
        }
    }

    @Override
    public void send(String recipient, String message) {
        System.out.println("[Reminder] To: " + recipient + " | " + message);
    }
}
