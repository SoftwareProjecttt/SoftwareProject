package com.appointmentsystem.strategy;

import com.appointmentsystem.domain.Appointment;

/**
 * Validates participant capacity for a booking.
 *
 * @author Mohammad
 * @version 1.0
 */
public class ParticipantLimitRuleStrategy implements BookingRuleStrategy {

    /**
     * Checks whether participant count fits within slot capacity.
     *
     * @param appointment appointment to validate
     * @return true if valid, otherwise false
     */
    @Override
    public boolean isValid(Appointment appointment) {
        return appointment.getParticipantCount() > 0
                && appointment.getParticipantCount() <= appointment.getTimeSlot().getRemainingCapacity();
    }

    /**
     * Returns validation message for participant limit rule.
     *
     * @return error message
     */
    @Override
    public String getErrorMessage() {
        return "Booking rejected: participant count exceeds available capacity or is invalid.";
    }
}