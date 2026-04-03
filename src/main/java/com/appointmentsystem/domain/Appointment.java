package com.appointmentsystem.domain;

import java.time.LocalDateTime;

/**
 * Represents an appointment booking.
 *
 * @author Mohammad
 * @version 3.0
 */
public class Appointment {

    /** Appointment identifier. */
    private final String id;

    /** Customer name. */
    private final String customerName;

    /** Selected time slot. */
    private TimeSlot timeSlot;

    /** Number of participants in the booking. */
    private final int participantCount;

    /** Current appointment status. */
    private AppointmentStatus status;

    /**
     * Creates a new appointment.
     *
     * @param id appointment id
     * @param customerName customer name
     * @param timeSlot selected slot
     * @param participantCount participant count
     * @param status appointment status
     */
    public Appointment(String id, String customerName, TimeSlot timeSlot,
                       int participantCount, AppointmentStatus status) {
        this.id = id;
        this.customerName = customerName;
        this.timeSlot = timeSlot;
        this.participantCount = participantCount;
        this.status = status;
    }

    /**
     * Returns appointment id.
     *
     * @return appointment id
     */
    public String getId() {
        return id;
    }

    /**
     * Returns customer name.
     *
     * @return customer name
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * Returns selected time slot.
     *
     * @return time slot
     */
    public TimeSlot getTimeSlot() {
        return timeSlot;
    }

    /**
     * Updates the appointment slot.
     *
     * @param timeSlot new time slot
     */
    public void setTimeSlot(TimeSlot timeSlot) {
        this.timeSlot = timeSlot;
    }

    /**
     * Returns participant count.
     *
     * @return participant count
     */
    public int getParticipantCount() {
        return participantCount;
    }

    /**
     * Returns appointment status.
     *
     * @return appointment status
     */
    public AppointmentStatus getStatus() {
        return status;
    }

    /**
     * Cancels the appointment.
     */
    public void cancel() {
        this.status = AppointmentStatus.CANCELLED;
    }

    /**
     * Checks whether the appointment belongs to the given customer name.
     *
     * @param customerName customer name to compare
     * @return true if matches, otherwise false
     */
    public boolean belongsToCustomer(String customerName) {
        if (customerName == null) {
            return false;
        }
        return this.customerName.equalsIgnoreCase(customerName.trim());
    }

    /**
     * Checks whether the appointment starts in the future.
     *
     * @return true if future, otherwise false
     */
    public boolean isFuture() {
        LocalDateTime appointmentDateTime = LocalDateTime.of(
                timeSlot.getDate(),
                timeSlot.getStartTime()
        );
        return appointmentDateTime.isAfter(LocalDateTime.now());
    }

    /**
     * Returns a text representation of the appointment.
     *
     * @return formatted appointment text
     */
    @Override
    public String toString() {
        return "Appointment ID: " + id
                + " | Customer: " + customerName
                + " | Slot: " + timeSlot.getId()
                + " | Date: " + timeSlot.getDate()
                + " | Time: " + timeSlot.getStartTime() + " - " + timeSlot.getEndTime()
                + " | Participants: " + participantCount
                + " | Status: " + status;
    }
}