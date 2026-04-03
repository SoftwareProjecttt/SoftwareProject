package com.appointmentsystem.strategy;

import com.appointmentsystem.domain.Appointment;

/**
 * Validates maximum appointment duration.
 *
 * @author Mohammad
 * @version 1.0
 */
public class DurationRuleStrategy implements BookingRuleStrategy {

    /** Maximum allowed duration in minutes. */
    private final long maxDurationInMinutes;

    /**
     * Creates a duration rule strategy.
     *
     * @param maxDurationInMinutes maximum allowed duration in minutes
     */
    public DurationRuleStrategy(long maxDurationInMinutes) {
        this.maxDurationInMinutes = maxDurationInMinutes;
    }

    /**
     * Checks whether appointment duration is within the allowed limit.
     *
     * @param appointment appointment to validate
     * @return true if valid, otherwise false
     */
    @Override
    public boolean isValid(Appointment appointment) {
        return appointment.getTimeSlot().getDurationInMinutes() <= maxDurationInMinutes;
    }

    /**
     * Returns validation message for duration rule.
     *
     * @return error message
     */
    @Override
    public String getErrorMessage() {
        return "Booking rejected: appointment duration exceeds the allowed limit.";
    }
}