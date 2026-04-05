package com.appointmentsystem.service;

import com.appointmentsystem.domain.Appointment;
import com.appointmentsystem.domain.AppointmentStatus;
import com.appointmentsystem.domain.TimeSlot;
import com.appointmentsystem.exception.AuthorizationException;
import com.appointmentsystem.exception.BookingException;
import com.appointmentsystem.observer.AppointmentEventType;
import com.appointmentsystem.observer.AppointmentObserver;
import com.appointmentsystem.persistence.AppointmentRepository;
import com.appointmentsystem.persistence.TimeSlotRepository;
import com.appointmentsystem.strategy.BookingRuleStrategy;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles appointment modification and cancellation for users and administrators.
 *
 * <p>Implements the Subject role of the Observer pattern.
 * Registered observers are notified after each successful cancel or modify.</p>
 *
 * @author Mohammad
 * @version 2.0
 */
public class ReservationManagementService {

    /** Repository for appointments. */
    private final AppointmentRepository appointmentRepository;

    /** Repository for time slots. */
    private final TimeSlotRepository timeSlotRepository;

    /** Booking rule strategies reused during modification. */
    private final List<BookingRuleStrategy> bookingRules;

    /** Authentication service for administrator authorization. */
    private final AuthService authService;

    /** Registered observers that receive cancel and modify notifications. */
    private final List<AppointmentObserver> observers = new ArrayList<>();

    /**
     * Creates a new reservation management service.
     *
     * @param appointmentRepository appointment repository
     * @param timeSlotRepository time slot repository
     * @param bookingRules booking validation rules
     * @param authService authentication service
     */
    public ReservationManagementService(AppointmentRepository appointmentRepository,
                                        TimeSlotRepository timeSlotRepository,
                                        List<BookingRuleStrategy> bookingRules,
                                        AuthService authService) {
        this.appointmentRepository = appointmentRepository;
        this.timeSlotRepository = timeSlotRepository;
        this.bookingRules = bookingRules;
        this.authService = authService;
    }

    // -------------------------------------------------------------------------
    // Observer management
    // -------------------------------------------------------------------------

    /**
     * Registers an observer to receive cancel and modify notifications.
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
    // Reservation management logic
    // -------------------------------------------------------------------------

    /**
     * Returns all reservations for administrators only.
     *
     * @return list of appointments
     */
    public List<Appointment> getAllReservationsByAdmin() {
        ensureAdminAuthorized();
        return appointmentRepository.findAll();
    }

    /**
     * Modifies a user's own future appointment.
     *
     * @param appointmentId appointment id
     * @param customerName customer name
     * @param newSlotId new slot id
     * @return modified appointment
     */
    public Appointment modifyAppointmentByUser(String appointmentId,
                                               String customerName,
                                               String newSlotId) {
        Appointment appointment = findAppointmentById(appointmentId);

        if (!appointment.belongsToCustomer(customerName)) {
            throw new BookingException("Modification rejected: appointment does not belong to this user.");
        }

        return modifyAppointmentInternal(appointment, newSlotId);
    }

    /**
     * Cancels a user's own future appointment.
     *
     * @param appointmentId appointment id
     * @param customerName customer name
     * @return cancelled appointment
     */
    public Appointment cancelAppointmentByUser(String appointmentId, String customerName) {
        Appointment appointment = findAppointmentById(appointmentId);

        if (!appointment.belongsToCustomer(customerName)) {
            throw new BookingException("Cancellation rejected: appointment does not belong to this user.");
        }

        return cancelAppointmentInternal(appointment);
    }

    /**
     * Modifies any reservation as an administrator.
     *
     * @param appointmentId appointment id
     * @param newSlotId new slot id
     * @return modified appointment
     */
    public Appointment modifyReservationByAdmin(String appointmentId, String newSlotId) {
        ensureAdminAuthorized();
        Appointment appointment = findAppointmentById(appointmentId);
        return modifyAppointmentInternal(appointment, newSlotId);
    }

    /**
     * Cancels any reservation as an administrator.
     *
     * @param appointmentId appointment id
     * @return cancelled appointment
     */
    public Appointment cancelReservationByAdmin(String appointmentId) {
        ensureAdminAuthorized();
        Appointment appointment = findAppointmentById(appointmentId);
        return cancelAppointmentInternal(appointment);
    }

    /**
     * Modifies an appointment by moving it to a different future slot.
     *
     * @param appointment appointment to modify
     * @param newSlotId new slot id
     * @return modified appointment
     */
    private Appointment modifyAppointmentInternal(Appointment appointment, String newSlotId) {
        ensureFutureConfirmedAppointment(appointment);

        TimeSlot currentSlot = appointment.getTimeSlot();
        TimeSlot newSlot = timeSlotRepository.findById(newSlotId)
                .orElseThrow(() -> new BookingException("Modification rejected: selected slot was not found."));

        if (!isFutureSlot(newSlot)) {
            throw new BookingException("Modification rejected: only future appointments can be scheduled.");
        }

        if (currentSlot.getId().equalsIgnoreCase(newSlot.getId())) {
            return appointment;
        }

        Appointment simulatedAppointment = new Appointment(
                appointment.getId(),
                appointment.getCustomerName(),
                newSlot,
                appointment.getParticipantCount(),
                AppointmentStatus.CONFIRMED
        );

        validateRules(simulatedAppointment);

        currentSlot.releaseParticipants(appointment.getParticipantCount());
        newSlot.bookParticipants(appointment.getParticipantCount());
        appointment.setTimeSlot(newSlot);

        notifyObservers(appointment, AppointmentEventType.MODIFIED);

        return appointment;
    }

    /**
     * Cancels an appointment and returns its slot capacity.
     *
     * @param appointment appointment to cancel
     * @return cancelled appointment
     */
    private Appointment cancelAppointmentInternal(Appointment appointment) {
        ensureFutureConfirmedAppointment(appointment);

        appointment.getTimeSlot().releaseParticipants(appointment.getParticipantCount());
        appointment.cancel();

        notifyObservers(appointment, AppointmentEventType.CANCELLED);

        return appointment;
    }

    /**
     * Finds an appointment by id.
     *
     * @param appointmentId appointment id
     * @return found appointment
     */
    private Appointment findAppointmentById(String appointmentId) {
        return appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new BookingException("Reservation not found."));
    }

    /**
     * Ensures the appointment can still be managed.
     *
     * @param appointment appointment to check
     */
    private void ensureFutureConfirmedAppointment(Appointment appointment) {
        if (appointment.getStatus() != AppointmentStatus.CONFIRMED) {
            throw new BookingException("Action rejected: only confirmed appointments can be modified or cancelled.");
        }

        if (!appointment.isFuture()) {
            throw new BookingException("Action rejected: only future appointments can be modified or cancelled.");
        }
    }

    /**
     * Validates booking rules on a simulated modified appointment.
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

    /**
     * Ensures that the current session belongs to an authenticated administrator.
     */
    private void ensureAdminAuthorized() {
        if (!authService.isAuthenticated()) {
            throw new AuthorizationException("Access denied: only administrators can perform this action.");
        }
    }

    /**
     * Checks whether the selected slot is in the future.
     *
     * @param timeSlot time slot
     * @return true if future, otherwise false
     */
    private boolean isFutureSlot(TimeSlot timeSlot) {
        LocalDateTime slotDateTime = LocalDateTime.of(timeSlot.getDate(), timeSlot.getStartTime());
        return slotDateTime.isAfter(LocalDateTime.now());
    }
}