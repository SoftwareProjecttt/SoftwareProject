package com.appointmentsystem.strategy;

import com.appointmentsystem.domain.Appointment;

/**
 * Strategy interface for validating booking rules.
 *
 * @author Mohammad
 * @version 1.0
 */
public interface BookingRuleStrategy {

    /**
     * Checks whether the booking satisfies this rule.
     *
     * @param appointment appointment to validate
     * @return true if valid, otherwise false
     */
    boolean isValid(Appointment appointment);

    /**
     * Returns the error message for this rule.
     *
     * @return validation error message
     */
    String getErrorMessage();
}