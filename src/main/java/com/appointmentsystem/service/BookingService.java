package com.appointmentsystem.service;

import com.appointmentsystem.domain.Appointment;
import com.appointmentsystem.domain.AppointmentStatus;
import com.appointmentsystem.domain.TimeSlot;
import com.appointmentsystem.exception.BookingException;
import com.appointmentsystem.observer.AppointmentEventType;
import com.appointmentsystem.observer.AppointmentObserver;
import com.appointmentsystem.persistence.AppointmentRepository;
import com.appointmentsystem.persistence.TimeSlotRepository;
import com.appointmentsystem.strategy.BookingRuleStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Handles appointment booking operations.
 *
 * <p>Implements the Subject role of the Observer pattern.
 * Registered observers are notified after each successful booking.</p>
 *
 * @author Mohammad
 * @version 2.0
 */
public class BookingService {

    /** Repository used to access time slots. */
    private final TimeSlotRepository timeSlotRepository;

    /** Repository used to save appointments. */
    private final AppointmentRepository appointmentRepository;

    /** Validation strategies used for booking rules. */
    private final List<BookingRuleStrategy> bookingRules;

    /** Registered observers that receive booking event notifications. */
    private final List<AppointmentObserver> observers = new ArrayList<>();

    /**
     * Creates a new booking service.
     *
     * @param timeSlotRepository    time slot repository
     * @param appointmentRepository appointment repository
     * @param bookingRules          booking rule strategies
     */
    public BookingService(TimeSlotRepository timeSlotRepository,
                          AppointmentRepository appointmentRepository,
                          List<BookingRuleStrategy> bookingRules) {
        this.timeSlotRepository = timeSlotRepository;
        this.appointmentRepository = appointmentRepository;
        this.bookingRules = bookingRules;
    }

    // -------------------------------------------------------------------------
    // Observer management
    // -------------------------------------------------------------------------

    /**
     * Registers an observer to receive appointment notifications.
     *
     * @param observer observer to register
     */
    public void registerObserver(AppointmentObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    /**
     * Removes a previously registered observer.
     *
     * @param observer observer to remove
     */
    public void removeObserver(AppointmentObserver observer) {
        observers.remove(observer);
    }

    /**
     * Notifies all registered observers of an appointment event.
     *
     * @param appointment the affected appointment
     * @param eventType   the type of event
     */
    private void notifyObservers(Appointment appointment, AppointmentEventType eventType) {
        for (AppointmentObserver observer : observers) {
            observer.update(appointment, eventType);
        }
    }

    // -------------------------------------------------------------------------
    // Booking logic
    // -------------------------------------------------------------------------

    /**
     * Books an appointment, stores it with confirmed status, and notifies observers.
     *
     * @param customerName    customer name
     * @param slotId          selected slot id
     * @param participantCount number of participants
     * @return saved appointment
     */
    public Appointment bookAppointment(String customerName, String slotId, int participantCount) {
        if (customerName == null || customerName.trim().isEmpty()) {
            throw new BookingException("Booking rejected: customer name is required.");
        }

        TimeSlot timeSlot = timeSlotRepository.findById(slotId)
                .orElseThrow(() -> new BookingException("Booking rejected: selected slot was not found."));

        Appointment appointment = new Appointment(
                UUID.randomUUID().toString(),
                customerName.trim(),
                timeSlot,
                participantCount,
                AppointmentStatus.CONFIRMED
        );

        validateRules(appointment);

        timeSlot.bookParticipants(participantCount);
        appointmentRepository.save(appointment);

        notifyObservers(appointment, AppointmentEventType.BOOKED);

        return appointment;
    }

    /**
     * Validates all booking rules.
     *
     * @param appointment appointment to validate
     */
    private void validateRules(Appointment appointment) {
        for (BookingRuleStrategy rule : bookingRules) {
            if (!rule.isValid(appointment)) {
                throw new BookingException(rule.getErrorMessage());
            }
        }
    }
}