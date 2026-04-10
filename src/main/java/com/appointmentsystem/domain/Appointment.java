package com.appointmentsystem.domain;

import java.time.Clock;
import java.time.LocalDateTime;

/**
 * Represents an appointment booking.
 *
 * @author Mohammad
 * @version 4.0
 */
public class Appointment {

    private final String id;
    private final String customerName;
    private TimeSlot timeSlot;
    private final int participantCount;
    private AppointmentStatus status;
    private final AppointmentType appointmentType;

    public Appointment(String id,
                       String customerName,
                       TimeSlot timeSlot,
                       int participantCount,
                       AppointmentStatus status,
                       AppointmentType appointmentType) {
        this.id = id;
        this.customerName = customerName;
        this.timeSlot = timeSlot;
        this.participantCount = participantCount;
        this.status = status;
        this.appointmentType = appointmentType;
    }

    public String getId() {
        return id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public TimeSlot getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(TimeSlot timeSlot) {
        this.timeSlot = timeSlot;
    }

    public int getParticipantCount() {
        return participantCount;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public AppointmentType getAppointmentType() {
        return appointmentType;
    }

    public void cancel() {
        this.status = AppointmentStatus.CANCELLED;
    }

    public boolean belongsToCustomer(String customerName) {
        if (customerName == null) {
            return false;
        }
        return this.customerName.equalsIgnoreCase(customerName.trim());
    }

    public boolean isFuture(Clock clock) {
        LocalDateTime appointmentDateTime = LocalDateTime.of(
                timeSlot.getDate(),
                timeSlot.getStartTime()
        );
        return appointmentDateTime.isAfter(LocalDateTime.now(clock));
    }

    public boolean isFuture() {
        return isFuture(Clock.systemDefaultZone());
    }

    @Override
    public String toString() {
        return "Appointment ID: " + id
                + " | Customer: " + customerName
                + " | Slot: " + timeSlot.getId()
                + " | Date: " + timeSlot.getDate()
                + " | Time: " + timeSlot.getStartTime() + " - " + timeSlot.getEndTime()
                + " | Participants: " + participantCount
                + " | Type: " + appointmentType
                + " | Status: " + status;
    }
}
