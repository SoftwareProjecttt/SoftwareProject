package com.appointmentsystem;

import com.appointmentsystem.domain.Appointment;
import com.appointmentsystem.exception.BookingException;
import com.appointmentsystem.observer.AppointmentEventType;
import com.appointmentsystem.observer.AppointmentObserver;
import com.appointmentsystem.strategy.BookingRuleStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * Shared support for appointment workflows:
 * booking-rule validation and observer notifications.
 */
public class AppointmentWorkflowSupport {

    private final List<BookingRuleStrategy> bookingRules;
    private final List<AppointmentObserver> observers = new ArrayList<>();

    public AppointmentWorkflowSupport(List<BookingRuleStrategy> bookingRules) {
        this.bookingRules = bookingRules;
    }

    public void registerObserver(AppointmentObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void removeObserver(AppointmentObserver observer) {
        observers.remove(observer);
    }

    public void notifyObservers(Appointment appointment, AppointmentEventType eventType) {
        for (AppointmentObserver observer : observers) {
            observer.update(appointment, eventType);
        }
    }

    public void validateRules(Appointment appointment) {
        for (BookingRuleStrategy rule : bookingRules) {
            if (!rule.isValid(appointment)) {
                throw new BookingException(rule.getErrorMessage());
            }
        }
    }
}
