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
 * Unit tests for {@link BookingService}.
 *
 * <p>Mocks: {@link TimeSlotRepository}, {@link AppointmentRepository}, and
 * individual {@link BookingRuleStrategy} instances where needed.
 * Real strategies ({@link DurationRuleStrategy}, {@link ParticipantLimitRuleStrategy})
 * are used for capacity and duration edge-case tests.</p>
 */
@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    // -------------------------------------------------------------------------
    // Mocks
    // -------------------------------------------------------------------------

    @Mock
    private TimeSlotRepository timeSlotRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private AppointmentObserver observer;

    // -------------------------------------------------------------------------
    // Test fixtures
    // -------------------------------------------------------------------------

    /** A valid future slot with enough capacity for most tests. */
    private TimeSlot validSlot;

    /** The service under test — re-created fresh before every test. */
    private BookingService bookingService;

    private static final String CUSTOMER  = "Sara";
    private static final String SLOT_ID   = "TS1";
    private static final int    PARTICIPANTS = 1;

    @BeforeEach
    void setUp() {
        // Slot: 09:00–10:00 (60 min), max 3, currently 1 booked → 2 remaining
        validSlot = new TimeSlot(
                SLOT_ID,
                LocalDate.now().plusDays(1),
                LocalTime.of(9, 0),
                LocalTime.of(10, 0),
                3,
                1
        );

        // Default service: real rules — 120-min max duration, participant limit
        List<BookingRuleStrategy> realRules = List.of(
                new DurationRuleStrategy(120),
                new ParticipantLimitRuleStrategy()
        );

        bookingService = new BookingService(timeSlotRepository, appointmentRepository, realRules);
        bookingService.registerObserver(observer);
    }

    // =========================================================================
    // 1. Successful booking
    // =========================================================================

    @Test
    @DisplayName("bookAppointment — success: saves appointment and returns CONFIRMED status")
    void bookAppointment_success_savesAppointment() {
        when(timeSlotRepository.findById(SLOT_ID)).thenReturn(Optional.of(validSlot));

        Appointment result = bookingService.bookAppointment(CUSTOMER, SLOT_ID, PARTICIPANTS);

        assertNotNull(result.getId());
        assertEquals(CUSTOMER, result.getCustomerName());
        assertEquals(AppointmentStatus.CONFIRMED, result.getStatus());
        assertEquals(PARTICIPANTS, result.getParticipantCount());
    }

    @Test
    @DisplayName("bookAppointment — success: repository.save() is called exactly once")
    void bookAppointment_success_callsRepositorySave() {
        when(timeSlotRepository.findById(SLOT_ID)).thenReturn(Optional.of(validSlot));

        bookingService.bookAppointment(CUSTOMER, SLOT_ID, PARTICIPANTS);

        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }

    @Test
    @DisplayName("bookAppointment — success: slot's booked count is incremented")
    void bookAppointment_success_incrementsSlotBookedCount() {
        when(timeSlotRepository.findById(SLOT_ID)).thenReturn(Optional.of(validSlot));
        int bookedBefore = validSlot.getBookedCount();

        bookingService.bookAppointment(CUSTOMER, SLOT_ID, PARTICIPANTS);

        assertEquals(bookedBefore + PARTICIPANTS, validSlot.getBookedCount());
    }

    // =========================================================================
    // 2. Customer name validation
    // =========================================================================

    @Test
    @DisplayName("bookAppointment — fails: null customer name throws BookingException")
    void bookAppointment_fails_whenCustomerNameIsNull() {
        BookingException ex = assertThrows(BookingException.class,
                () -> bookingService.bookAppointment(null, SLOT_ID, PARTICIPANTS));

        assertTrue(ex.getMessage().contains("customer name is required"));
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    @DisplayName("bookAppointment — fails: blank customer name throws BookingException")
    void bookAppointment_fails_whenCustomerNameIsBlank() {
        BookingException ex = assertThrows(BookingException.class,
                () -> bookingService.bookAppointment("   ", SLOT_ID, PARTICIPANTS));

        assertTrue(ex.getMessage().contains("customer name is required"));
        verify(appointmentRepository, never()).save(any());
    }

    // =========================================================================
    // 3. Slot lookup failure
    // =========================================================================

    @Test
    @DisplayName("bookAppointment — fails: unknown slot ID throws BookingException")
    void bookAppointment_fails_whenSlotNotFound() {
        when(timeSlotRepository.findById("UNKNOWN")).thenReturn(Optional.empty());

        BookingException ex = assertThrows(BookingException.class,
                () -> bookingService.bookAppointment(CUSTOMER, "UNKNOWN", PARTICIPANTS));

        assertTrue(ex.getMessage().contains("slot was not found"));
        verify(appointmentRepository, never()).save(any());
    }

    // =========================================================================
    // 4. Duration rule violation  (mocked strategy)
    // =========================================================================

    @Test
    @DisplayName("bookAppointment — fails: duration rule violation throws BookingException")
    void bookAppointment_fails_whenDurationRuleViolated() {
        // Slot: 09:00–12:00 = 180 min — exceeds the 120-min max
        TimeSlot longSlot = new TimeSlot(
                "TS_LONG",
                LocalDate.now().plusDays(1),
                LocalTime.of(9, 0),
                LocalTime.of(12, 0), // 3 hours
                5,
                0
        );
        when(timeSlotRepository.findById("TS_LONG")).thenReturn(Optional.of(longSlot));

        BookingException ex = assertThrows(BookingException.class,
                () -> bookingService.bookAppointment(CUSTOMER, "TS_LONG", PARTICIPANTS));

        assertTrue(ex.getMessage().contains("duration exceeds the allowed limit"));
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    @DisplayName("bookAppointment — fails: mocked rule rejection throws BookingException with rule message")
    void bookAppointment_fails_whenMockedRuleRejects() {
        // Replace service with one backed by a single mocked rule that always fails
        BookingRuleStrategy alwaysFail = mock(BookingRuleStrategy.class);
        when(alwaysFail.isValid(any())).thenReturn(false);
        when(alwaysFail.getErrorMessage()).thenReturn("Custom rule violation.");

        BookingService serviceWithMockedRule = new BookingService(
                timeSlotRepository, appointmentRepository, List.of(alwaysFail));

        when(timeSlotRepository.findById(SLOT_ID)).thenReturn(Optional.of(validSlot));

        BookingException ex = assertThrows(BookingException.class,
                () -> serviceWithMockedRule.bookAppointment(CUSTOMER, SLOT_ID, PARTICIPANTS));

        assertEquals("Custom rule violation.", ex.getMessage());
        verify(appointmentRepository, never()).save(any());
    }

    // =========================================================================
    // 5. Participant limit exceeded
    // =========================================================================

    @Test
    @DisplayName("bookAppointment — fails: participant count exceeds remaining capacity")
    void bookAppointment_fails_whenParticipantCountExceedsRemainingCapacity() {
        // validSlot has 2 remaining; requesting 3 should violate ParticipantLimitRuleStrategy
        when(timeSlotRepository.findById(SLOT_ID)).thenReturn(Optional.of(validSlot));

        BookingException ex = assertThrows(BookingException.class,
                () -> bookingService.bookAppointment(CUSTOMER, SLOT_ID, 3));

        assertTrue(ex.getMessage().contains("participant count exceeds"));
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    @DisplayName("bookAppointment — fails: participant count of zero is invalid")
    void bookAppointment_fails_whenParticipantCountIsZero() {
        when(timeSlotRepository.findById(SLOT_ID)).thenReturn(Optional.of(validSlot));

        BookingException ex = assertThrows(BookingException.class,
                () -> bookingService.bookAppointment(CUSTOMER, SLOT_ID, 0));

        assertTrue(ex.getMessage().contains("participant count exceeds"));
        verify(appointmentRepository, never()).save(any());
    }

    // =========================================================================
    // 6. Slot is completely full
    // =========================================================================

    @Test
    @DisplayName("bookAppointment — fails: slot is fully booked (no remaining capacity)")
    void bookAppointment_fails_whenSlotIsFull() {
        // max = 2, booked = 2 → 0 remaining
        TimeSlot fullSlot = new TimeSlot(
                "TS_FULL",
                LocalDate.now().plusDays(1),
                LocalTime.of(11, 0),
                LocalTime.of(12, 0),
                2,
                2  // fully booked
        );
        when(timeSlotRepository.findById("TS_FULL")).thenReturn(Optional.of(fullSlot));

        BookingException ex = assertThrows(BookingException.class,
                () -> bookingService.bookAppointment(CUSTOMER, "TS_FULL", 1));

        assertTrue(ex.getMessage().contains("participant count exceeds"));
        verify(appointmentRepository, never()).save(any());
    }

    // =========================================================================
    // 7. Observer notifications
    // =========================================================================

    @Test
    @DisplayName("bookAppointment — observer is notified with event type BOOKED on success")
    void bookAppointment_success_notifiesObserverWithBookedEvent() {
        when(timeSlotRepository.findById(SLOT_ID)).thenReturn(Optional.of(validSlot));

        bookingService.bookAppointment(CUSTOMER, SLOT_ID, PARTICIPANTS);

        // Verify observer.update() was called once with the correct event type
        ArgumentCaptor<Appointment> appointmentCaptor = ArgumentCaptor.forClass(Appointment.class);
        ArgumentCaptor<AppointmentEventType> eventCaptor = ArgumentCaptor.forClass(AppointmentEventType.class);
        verify(observer, times(1)).update(appointmentCaptor.capture(), eventCaptor.capture());

        assertEquals(AppointmentEventType.BOOKED, eventCaptor.getValue());
        assertEquals(CUSTOMER, appointmentCaptor.getValue().getCustomerName());
    }

    @Test
    @DisplayName("bookAppointment — observer is NOT notified when booking fails")
    void bookAppointment_fails_doesNotNotifyObserver() {
        when(timeSlotRepository.findById(SLOT_ID)).thenReturn(Optional.of(validSlot));

        // Attempt a booking that will fail the participant limit
        assertThrows(BookingException.class,
                () -> bookingService.bookAppointment(CUSTOMER, SLOT_ID, 99));

        verify(observer, never()).update(any(), any());
    }

    @Test
    @DisplayName("registerObserver — duplicate registration is ignored")
    void registerObserver_duplicateIsIgnored() {
        // observer is already registered in @BeforeEach
        bookingService.registerObserver(observer); // register again — should be ignored

        when(timeSlotRepository.findById(SLOT_ID)).thenReturn(Optional.of(validSlot));
        bookingService.bookAppointment(CUSTOMER, SLOT_ID, PARTICIPANTS);

        // Must still be notified exactly once, not twice
        verify(observer, times(1)).update(any(Appointment.class), eq(AppointmentEventType.BOOKED));
    }

    @Test
    @DisplayName("removeObserver — removed observer receives no further notifications")
    void removeObserver_removedObserverIsNotNotified() {
        bookingService.removeObserver(observer);

        when(timeSlotRepository.findById(SLOT_ID)).thenReturn(Optional.of(validSlot));
        bookingService.bookAppointment(CUSTOMER, SLOT_ID, PARTICIPANTS);

        verify(observer, never()).update(any(), any());
    }

    // =========================================================================
    // 8. No-rule configuration
    // =========================================================================

    @Test
    @DisplayName("bookAppointment — succeeds even when no booking rules are configured")
    void bookAppointment_success_withNoRules() {
        BookingService noRuleService = new BookingService(
                timeSlotRepository, appointmentRepository, Collections.emptyList());

        when(timeSlotRepository.findById(SLOT_ID)).thenReturn(Optional.of(validSlot));

        Appointment result = noRuleService.bookAppointment(CUSTOMER, SLOT_ID, PARTICIPANTS);

        assertNotNull(result);
        assertEquals(AppointmentStatus.CONFIRMED, result.getStatus());
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }
}
