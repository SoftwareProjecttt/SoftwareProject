package com.appointmentsystem;

import com.appointmentsystem.domain.Appointment;
import com.appointmentsystem.observer.AppointmentEventType;
import com.appointmentsystem.observer.AppointmentObserver;

/**
 * Notification service that reacts to appointment lifecycle events
 * and dispatches them via a NotificationGateway.
 */
public class NotificationService implements AppointmentObserver {

    private final NotificationGateway notificationGateway;

    public NotificationService(NotificationGateway notificationGateway) {
        this.notificationGateway = notificationGateway;
    }

    @Override
    public void update(Appointment appointment, AppointmentEventType eventType) {
        String recipient = appointment.getCustomerName() + "@example.com";
        String message = "";
        
        switch (eventType) {
            case BOOKED:
                message = "Appointment booked for "
                        + appointment.getCustomerName()
                        + " on " + appointment.getTimeSlot().getDate()
                        + " at " + appointment.getTimeSlot().getStartTime()
                        + " (ID: " + appointment.getId() + ")";
                break;

            case CANCELLED:
                message = "Appointment cancelled for "
                        + appointment.getCustomerName()
                        + " on " + appointment.getTimeSlot().getDate()
                        + " at " + appointment.getTimeSlot().getStartTime()
                        + " (ID: " + appointment.getId() + ")";
                break;

            case MODIFIED:
                message = "Appointment modified for "
                        + appointment.getCustomerName()
                        + " new slot on " + appointment.getTimeSlot().getDate()
                        + " at " + appointment.getTimeSlot().getStartTime()
                        + " (ID: " + appointment.getId() + ")";
                break;
        }

        notificationGateway.send(recipient, message);
    }
}
