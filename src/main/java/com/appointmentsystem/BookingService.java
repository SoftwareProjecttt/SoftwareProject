package com.appointmentsystem;

import com.appointmentsystem.domain.Appointment;
import com.appointmentsystem.domain.AppointmentStatus;
import com.appointmentsystem.domain.AppointmentType;
import com.appointmentsystem.domain.TimeSlot;
import com.appointmentsystem.exception.BookingException;
import com.appointmentsystem.observer.AppointmentEventType;
import com.appointmentsystem.observer.AppointmentObserver;
import com.appointmentsystem.persistence.AppointmentRepository;
import com.appointmentsystem.persistence.TimeSlotRepository;
import com.appointmentsystem.strategy.BookingRuleStrategy;

import java.util.List;
import java.util.UUID;

/**
 * Handles appointment booking operations.
 *
 * @author Mohammad
 * @version 3.0
 */
public class BookingService {

    private final TimeSlotRepository timeSlotRepository;
    private final AppointmentRepository appointmentRepository;
    private final AppointmentWorkflowSupport workflowSupport;

    public BookingService(TimeSlotRepository timeSlotRepository,
                          AppointmentRepository appointmentRepository,
                          List<BookingRuleStrategy> bookingRules) {
        this.timeSlotRepository = timeSlotRepository;
        this.appointmentRepository = appointmentRepository;
        this.workflowSupport = new AppointmentWorkflowSupport(bookingRules);
    }

    public void registerObserver(AppointmentObserver observer) {
        workflowSupport.registerObserver(observer);
    }

    public void removeObserver(AppointmentObserver observer) {
        workflowSupport.removeObserver(observer);
    }

    public Appointment bookAppointment(String customerName,
                                       String slotId,
                                       int participantCount,
                                       AppointmentType appointmentType) {
        if (customerName == null || customerName.trim().isEmpty()) {
            throw new BookingException("Booking rejected: customer name is required.");
        }

        if (appointmentType == null) {
            throw new BookingException("Booking rejected: appointment type is required.");
        }

        TimeSlot timeSlot = timeSlotRepository.findById(slotId)
                .orElseThrow(() -> new BookingException("Booking rejected: selected slot was not found."));

        Appointment appointment = new Appointment(
                UUID.randomUUID().toString(),
                customerName.trim(),
                timeSlot,
                participantCount,
                AppointmentStatus.CONFIRMED,
                appointmentType
        );

        workflowSupport.validateRules(appointment);

        timeSlot.bookParticipants(participantCount);
        appointmentRepository.save(appointment);

        workflowSupport.notifyObservers(appointment, AppointmentEventType.BOOKED);

        return appointment;
    }
}
