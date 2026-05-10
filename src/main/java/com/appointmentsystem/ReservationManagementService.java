package com.appointmentsystem;

import com.appointmentsystem.domain.Appointment;
import com.appointmentsystem.domain.AppointmentStatus;
import com.appointmentsystem.exception.AuthorizationException;
import com.appointmentsystem.exception.BookingException;
import com.appointmentsystem.observer.AppointmentEventType;
import com.appointmentsystem.observer.AppointmentObserver;
import com.appointmentsystem.persistence.AppointmentRepository;
import com.appointmentsystem.persistence.TimeSlotRepository;
import com.appointmentsystem.strategy.BookingRuleStrategy;
import com.appointmentsystem.domain.TimeSlot;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Handles appointment modification and cancellation for users and administrators.
 *
 * @author Mohammad
 * @version 3.0
 */
public class ReservationManagementService {

    private final AppointmentRepository appointmentRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final AuthService authService;
    private final Clock clock;
    private final AppointmentWorkflowSupport workflowSupport;

    public ReservationManagementService(AppointmentRepository appointmentRepository,
                                        TimeSlotRepository timeSlotRepository,
                                        List<BookingRuleStrategy> bookingRules,
                                        AuthService authService,
                                        Clock clock) {
        this.appointmentRepository = appointmentRepository;
        this.timeSlotRepository = timeSlotRepository;
        this.authService = authService;
        this.clock = clock;
        this.workflowSupport = new AppointmentWorkflowSupport(bookingRules);
    }

    public void registerObserver(AppointmentObserver observer) {
        workflowSupport.registerObserver(observer);
    }

    public void removeObserver(AppointmentObserver observer) {
        workflowSupport.removeObserver(observer);
    }

    public List<Appointment> getAllReservationsByAdmin() {
        ensureAdminAuthorized();
        return appointmentRepository.findAll();
    }

    public Appointment modifyAppointmentByUser(String appointmentId,
                                               String customerName,
                                               String newSlotId) {
        Appointment appointment = findAppointmentById(appointmentId);

        if (!appointment.belongsToCustomer(customerName)) {
            throw new BookingException("Modification rejected: appointment does not belong to this user.");
        }

        return modifyAppointmentInternal(appointment, newSlotId);
    }

    public Appointment cancelAppointmentByUser(String appointmentId, String customerName) {
        Appointment appointment = findAppointmentById(appointmentId);

        if (!appointment.belongsToCustomer(customerName)) {
            throw new BookingException("Cancellation rejected: appointment does not belong to this user.");
        }

        return cancelAppointmentInternal(appointment);
    }

    public Appointment modifyReservationByAdmin(String appointmentId, String newSlotId) {
        ensureAdminAuthorized();
        Appointment appointment = findAppointmentById(appointmentId);
        return modifyAppointmentInternal(appointment, newSlotId);
    }

    public Appointment cancelReservationByAdmin(String appointmentId) {
        ensureAdminAuthorized();
        Appointment appointment = findAppointmentById(appointmentId);
        return cancelAppointmentInternal(appointment);
    }

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
                AppointmentStatus.CONFIRMED,
                appointment.getAppointmentType()
        );

        workflowSupport.validateRules(simulatedAppointment);

        currentSlot.releaseParticipants(appointment.getParticipantCount());
        newSlot.bookParticipants(appointment.getParticipantCount());
        appointment.setTimeSlot(newSlot);
        appointmentRepository.save(appointment);

        workflowSupport.notifyObservers(appointment, AppointmentEventType.MODIFIED);

        return appointment;
    }

    private Appointment cancelAppointmentInternal(Appointment appointment) {
        ensureFutureConfirmedAppointment(appointment);

        appointment.getTimeSlot().releaseParticipants(appointment.getParticipantCount());
        appointment.cancel();
        appointmentRepository.save(appointment);

        workflowSupport.notifyObservers(appointment, AppointmentEventType.CANCELLED);

        return appointment;
    }

    private Appointment findAppointmentById(String appointmentId) {
        return appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new BookingException("Reservation not found."));
    }

    private void ensureFutureConfirmedAppointment(Appointment appointment) {
        if (appointment.getStatus() != AppointmentStatus.CONFIRMED) {
            throw new BookingException("Action rejected: only confirmed appointments can be modified or cancelled.");
        }

        if (!appointment.isFuture(clock)) {
            throw new BookingException("Action rejected: only future appointments can be modified or cancelled.");
        }
    }

    private void ensureAdminAuthorized() {
        if (!authService.isAuthenticated()) {
            throw new AuthorizationException("Access denied: only administrators can perform this action.");
        }
    }

    private boolean isFutureSlot(TimeSlot timeSlot) {
        LocalDateTime slotDateTime = LocalDateTime.of(timeSlot.getDate(), timeSlot.getStartTime());
        return slotDateTime.isAfter(LocalDateTime.now(clock));
    }
}
