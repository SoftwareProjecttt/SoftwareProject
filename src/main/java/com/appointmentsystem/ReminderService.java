package com.appointmentsystem;

import com.appointmentsystem.domain.Appointment;
import com.appointmentsystem.domain.AppointmentStatus;
import com.appointmentsystem.persistence.AppointmentRepository;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Sends reminders for upcoming appointments.
 *
 * @author Mohammad
 * @version 1.0
 */
public class ReminderService {

    private final AppointmentRepository appointmentRepository;
    private final NotificationGateway notificationGateway;
    private final Clock clock;

    public ReminderService(AppointmentRepository appointmentRepository,
                           NotificationGateway notificationGateway,
                           Clock clock) {
        this.appointmentRepository = appointmentRepository;
        this.notificationGateway = notificationGateway;
        this.clock = clock;
    }

    public List<String> sendUpcomingReminders() {
        List<String> sentMessages = new ArrayList<>();

        for (Appointment appointment : appointmentRepository.findAll()) {
            if (appointment.getStatus() != AppointmentStatus.CONFIRMED) {
                continue;
            }

            LocalDateTime appointmentTime = LocalDateTime.of(
                    appointment.getTimeSlot().getDate(),
                    appointment.getTimeSlot().getStartTime()
            );

            Duration duration = Duration.between(LocalDateTime.now(clock), appointmentTime);

            if (!duration.isNegative() && duration.toHours() <= 24) {
                String message = "Reminder: appointment for "
                        + appointment.getCustomerName()
                        + " on " + appointment.getTimeSlot().getDate()
                        + " at " + appointment.getTimeSlot().getStartTime();

                notificationGateway.send(appointment.getCustomerName(), message);
                sentMessages.add(message);
            }
        }

        return sentMessages;
    }
}
