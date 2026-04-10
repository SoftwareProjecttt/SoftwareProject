package com.appointmentsystem.strategy;

import com.appointmentsystem.domain.Appointment;
import com.appointmentsystem.domain.AppointmentType;

/**
 * Validates type-specific booking rules.
 *
 * @author Mohammad
 * @version 1.0
 */
public class AppointmentTypeRuleStrategy implements BookingRuleStrategy {

    private String errorMessage = "Invalid appointment type rule.";

    @Override
    public boolean isValid(Appointment appointment) {
        AppointmentType type = appointment.getAppointmentType();

        if (type == AppointmentType.INDIVIDUAL && appointment.getParticipantCount() != 1) {
            errorMessage = "Individual appointments allow exactly one participant.";
            return false;
        }

        if (type == AppointmentType.GROUP && appointment.getParticipantCount() < 2) {
            errorMessage = "Group appointments require at least two participants.";
            return false;
        }

        if (type == AppointmentType.URGENT
                && appointment.getTimeSlot().getDurationInMinutes() > 30) {
            errorMessage = "Urgent appointments cannot exceed 30 minutes.";
            return false;
        }

        return true;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }
}
