package com.appointmentsystem.service;

import com.appointmentsystem.BookingService;
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
import com.appointmentsystem.strategy.DurationRuleStrategy;
import com.appointmentsystem.strategy.ParticipantLimitRuleStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for BookingService.
 *
 * @author Mohammad
 * @version 2.0
 */
@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private TimeSlotRepository timeSlotRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private AppointmentObserver observer;

    private TimeSlot validSlot;
    private BookingService bookingService;

    private static final String CUSTOMER = "Sara";
    private static final String SLOT_ID = "TS1";
    private static final int PARTICIPANTS = 1;

    @BeforeEach
    void setUp() {
        validSlot = new TimeSlot(
                SLOT_ID,
                LocalDate.now().plusDays(1),
                LocalTime.of(9, 0),
                LocalTime.of(10, 0),
                3,
                1
        );

        List<BookingRuleStrategy> realRules = List.of(
                new DurationRuleStrategy(120),
                new ParticipantLimitRuleStrategy()
        );

        bookingService = new BookingService(timeSlotRepository, appointmentRepository, realRules);
        bookingService.registerObserver(observer);
    }

    @Test
    @DisplayName("bookAppointment success saves appointment")
    void bookAppointment_success_savesAppointment() {
        when(timeSlotRepository.findById(SLOT_ID)).thenReturn(Optional.of(validSlot));

        Appointment result = bookingService.bookAppointment(
                CUSTOMER,
                SLOT_ID,
                PARTICIPANTS,
                AppointmentType.INDIVIDUAL
        );

        assertNotNull(result.getId());
        assertEquals(CUSTOMER, result.getCustomerName());
        assertEquals(AppointmentStatus.CONFIRMED, result.getStatus());
        assertEquals(PARTICIPANTS, result.getParticipantCount());
        assertEquals(AppointmentType.INDIVIDUAL, result.getAppointmentType());
    }

    @Test
    @DisplayName("bookAppointment success calls repository save")
    void bookAppointment_success_callsRepositorySave() {
        when(timeSlotRepository.findById(SLOT_ID)).thenReturn(Optional.of(validSlot));

        bookingService.bookAppointment(
                CUSTOMER,
                SLOT_ID,
                PARTICIPANTS,
                AppointmentType.INDIVIDUAL
        );

        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }

    @Test
    @DisplayName("bookAppointment success increments slot booked count")
    void bookAppointment_success_incrementsSlotBookedCount() {
        when(timeSlotRepository.findById(SLOT_ID)).thenReturn(Optional.of(validSlot));
        int bookedBefore = validSlot.getBookedCount();

        bookingService.bookAppointment(
                CUSTOMER,
                SLOT_ID,
                PARTICIPANTS,
                AppointmentType.INDIVIDUAL
        );

        assertEquals(bookedBefore + PARTICIPANTS, validSlot.getBookedCount());
    }

    @Test
    @DisplayName("bookAppointment fails when customer name is null")
    void bookAppointment_fails_whenCustomerNameIsNull() {
        BookingException ex = assertThrows(BookingException.class,
                () -> bookingService.bookAppointment(null, SLOT_ID, PARTICIPANTS, AppointmentType.INDIVIDUAL));

        assertTrue(ex.getMessage().contains("customer name is required"));
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    @DisplayName("bookAppointment fails when customer name is blank")
    void bookAppointment_fails_whenCustomerNameIsBlank() {
        BookingException ex = assertThrows(BookingException.class,
                () -> bookingService.bookAppointment("   ", SLOT_ID, PARTICIPANTS, AppointmentType.INDIVIDUAL));

        assertTrue(ex.getMessage().contains("customer name is required"));
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    @DisplayName("bookAppointment fails when slot not found")
    void bookAppointment_fails_whenSlotNotFound() {
        when(timeSlotRepository.findById("UNKNOWN")).thenReturn(Optional.empty());

        BookingException ex = assertThrows(BookingException.class,
                () -> bookingService.bookAppointment(CUSTOMER, "UNKNOWN", PARTICIPANTS, AppointmentType.INDIVIDUAL));

        assertTrue(ex.getMessage().contains("slot was not found"));
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    @DisplayName("bookAppointment fails when duration rule violated")
    void bookAppointment_fails_whenDurationRuleViolated() {
        TimeSlot longSlot = new TimeSlot(
                "TS_LONG",
                LocalDate.now().plusDays(1),
                LocalTime.of(9, 0),
                LocalTime.of(12, 0),
                5,
                0
        );
        when(timeSlotRepository.findById("TS_LONG")).thenReturn(Optional.of(longSlot));

        BookingException ex = assertThrows(BookingException.class,
                () -> bookingService.bookAppointment(CUSTOMER, "TS_LONG", PARTICIPANTS, AppointmentType.INDIVIDUAL));

        assertTrue(ex.getMessage().contains("duration exceeds the allowed limit"));
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    @DisplayName("bookAppointment fails when mocked rule rejects")
    void bookAppointment_fails_whenMockedRuleRejects() {
        BookingRuleStrategy alwaysFail = mock(BookingRuleStrategy.class);
        when(alwaysFail.isValid(any())).thenReturn(false);
        when(alwaysFail.getErrorMessage()).thenReturn("Custom rule violation.");

        BookingService serviceWithMockedRule = new BookingService(
                timeSlotRepository,
                appointmentRepository,
                List.of(alwaysFail)
        );

        when(timeSlotRepository.findById(SLOT_ID)).thenReturn(Optional.of(validSlot));

        BookingException ex = assertThrows(BookingException.class,
                () -> serviceWithMockedRule.bookAppointment(CUSTOMER, SLOT_ID, PARTICIPANTS, AppointmentType.INDIVIDUAL));

        assertEquals("Custom rule violation.", ex.getMessage());
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    @DisplayName("bookAppointment fails when participant count exceeds remaining capacity")
    void bookAppointment_fails_whenParticipantCountExceedsRemainingCapacity() {
        when(timeSlotRepository.findById(SLOT_ID)).thenReturn(Optional.of(validSlot));

        BookingException ex = assertThrows(BookingException.class,
                () -> bookingService.bookAppointment(CUSTOMER, SLOT_ID, 3, AppointmentType.GROUP));

        assertTrue(ex.getMessage().contains("participant count exceeds"));
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    @DisplayName("bookAppointment fails when participant count is zero")
    void bookAppointment_fails_whenParticipantCountIsZero() {
        when(timeSlotRepository.findById(SLOT_ID)).thenReturn(Optional.of(validSlot));

        BookingException ex = assertThrows(BookingException.class,
                () -> bookingService.bookAppointment(CUSTOMER, SLOT_ID, 0, AppointmentType.INDIVIDUAL));

        assertTrue(ex.getMessage().contains("participant count exceeds"));
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    @DisplayName("bookAppointment fails when slot is full")
    void bookAppointment_fails_whenSlotIsFull() {
        TimeSlot fullSlot = new TimeSlot(
                "TS_FULL",
                LocalDate.now().plusDays(1),
                LocalTime.of(11, 0),
                LocalTime.of(12, 0),
                2,
                2
        );
        when(timeSlotRepository.findById("TS_FULL")).thenReturn(Optional.of(fullSlot));

        BookingException ex = assertThrows(BookingException.class,
                () -> bookingService.bookAppointment(CUSTOMER, "TS_FULL", 1, AppointmentType.INDIVIDUAL));

        assertTrue(ex.getMessage().contains("participant count exceeds"));
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    @DisplayName("bookAppointment success notifies observer with BOOKED event")
    void bookAppointment_success_notifiesObserverWithBookedEvent() {
        when(timeSlotRepository.findById(SLOT_ID)).thenReturn(Optional.of(validSlot));

        bookingService.bookAppointment(CUSTOMER, SLOT_ID, PARTICIPANTS, AppointmentType.INDIVIDUAL);

        ArgumentCaptor<Appointment> appointmentCaptor = ArgumentCaptor.forClass(Appointment.class);
        ArgumentCaptor<AppointmentEventType> eventCaptor = ArgumentCaptor.forClass(AppointmentEventType.class);
        verify(observer, times(1)).update(appointmentCaptor.capture(), eventCaptor.capture());

        assertEquals(AppointmentEventType.BOOKED, eventCaptor.getValue());
        assertEquals(CUSTOMER, appointmentCaptor.getValue().getCustomerName());
    }

    @Test
    @DisplayName("bookAppointment fails does not notify observer")
    void bookAppointment_fails_doesNotNotifyObserver() {
        when(timeSlotRepository.findById(SLOT_ID)).thenReturn(Optional.of(validSlot));

        assertThrows(BookingException.class,
                () -> bookingService.bookAppointment(CUSTOMER, SLOT_ID, 99, AppointmentType.GROUP));

        verify(observer, never()).update(any(), any());
    }

    @Test
    @DisplayName("registerObserver duplicate is ignored")
    void registerObserver_duplicateIsIgnored() {
        bookingService.registerObserver(observer);

        when(timeSlotRepository.findById(SLOT_ID)).thenReturn(Optional.of(validSlot));
        bookingService.bookAppointment(CUSTOMER, SLOT_ID, PARTICIPANTS, AppointmentType.INDIVIDUAL);

        verify(observer, times(1)).update(any(Appointment.class), eq(AppointmentEventType.BOOKED));
    }

    @Test
    @DisplayName("removeObserver removed observer is not notified")
    void removeObserver_removedObserverIsNotNotified() {
        bookingService.removeObserver(observer);

        when(timeSlotRepository.findById(SLOT_ID)).thenReturn(Optional.of(validSlot));
        bookingService.bookAppointment(CUSTOMER, SLOT_ID, PARTICIPANTS, AppointmentType.INDIVIDUAL);

        verify(observer, never()).update(any(), any());
    }

    @Test
    @DisplayName("bookAppointment succeeds with no rules")
    void bookAppointment_success_withNoRules() {
        BookingService noRuleService = new BookingService(
                timeSlotRepository,
                appointmentRepository,
                Collections.emptyList()
        );

        when(timeSlotRepository.findById(SLOT_ID)).thenReturn(Optional.of(validSlot));

        Appointment result = noRuleService.bookAppointment(
                CUSTOMER,
                SLOT_ID,
                PARTICIPANTS,
                AppointmentType.INDIVIDUAL
        );

        assertNotNull(result);
        assertEquals(AppointmentStatus.CONFIRMED, result.getStatus());
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }
}
