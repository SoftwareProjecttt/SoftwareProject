package com.appointmentsystem.service;

import com.appointmentsystem.AuthService;
import com.appointmentsystem.ReservationManagementService;
import com.appointmentsystem.domain.*;
import com.appointmentsystem.exception.AuthorizationException;
import com.appointmentsystem.exception.BookingException;
import com.appointmentsystem.observer.AppointmentEventType;
import com.appointmentsystem.observer.AppointmentObserver;
import com.appointmentsystem.persistence.AdminRepository;
import com.appointmentsystem.persistence.AppointmentRepository;
import com.appointmentsystem.persistence.TimeSlotRepository;
import com.appointmentsystem.security.SessionManager;
import com.appointmentsystem.strategy.BookingRuleStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReservationManagementServiceTest {

    private AppointmentRepository appointmentRepository;
    private TimeSlotRepository timeSlotRepository;
    private BookingRuleStrategy rule;

    // ✅ real objects بدل mocks
    private SessionManager sessionManager;
    private AuthService authService;

    private ReservationManagementService service;

    private AppointmentObserver observer;

    private TimeSlot oldSlot;
    private TimeSlot newSlot;
    private Appointment appointment;

    @BeforeEach
    void setUp() {
        appointmentRepository = mock(AppointmentRepository.class);
        timeSlotRepository = mock(TimeSlotRepository.class);
        rule = mock(BookingRuleStrategy.class);

        // ✅ real AuthService
        sessionManager = new SessionManager();
        AdminRepository adminRepository = mock(AdminRepository.class);

        authService = new AuthService(adminRepository, sessionManager);

        service = new ReservationManagementService(
                appointmentRepository,
                timeSlotRepository,
                List.of(rule),
                authService
        );

        observer = mock(AppointmentObserver.class);
        service.registerObserver(observer);

        oldSlot = new TimeSlot(
                "S1",
                LocalDate.now().plusDays(1),
                LocalTime.of(10, 0),
                LocalTime.of(11, 0),
                10,
                2
        );

        newSlot = new TimeSlot(
                "S2",
                LocalDate.now().plusDays(2),
                LocalTime.of(12, 0),
                LocalTime.of(13, 0),
                10,
                0
        );

        appointment = new Appointment(
                "A1",
                "Ahmad",
                oldSlot,
                2,
                AppointmentStatus.CONFIRMED
        );

        when(appointmentRepository.findById("A1")).thenReturn(Optional.of(appointment));
        when(timeSlotRepository.findById("S2")).thenReturn(Optional.of(newSlot));
        when(rule.isValid(any())).thenReturn(true);
    }

    // -----------------------------
    // ✅ MODIFY (USER)
    // -----------------------------

    @Test
    void modifyAppointmentByUser_success() {
        service.modifyAppointmentByUser("A1", "Ahmad", "S2");

        assertEquals(newSlot, appointment.getTimeSlot());
        assertEquals(0, oldSlot.getBookedCount());
        assertEquals(2, newSlot.getBookedCount());

        verify(observer).update(eq(appointment), eq(AppointmentEventType.MODIFIED));
    }

    @Test
    void modifyAppointmentByUser_wrongOwner() {
        assertThrows(BookingException.class,
                () -> service.modifyAppointmentByUser("A1", "Wrong", "S2"));
    }

    // -----------------------------
    // ❌ MODIFY FAIL CASES
    // -----------------------------

    @Test
    void modifyAppointment_slotNotFound() {
        when(timeSlotRepository.findById("S2")).thenReturn(Optional.empty());

        assertThrows(BookingException.class,
                () -> service.modifyAppointmentByUser("A1", "Ahmad", "S2"));
    }

    @Test
    void modifyAppointment_sameSlot_noChange() {
        when(timeSlotRepository.findById("S1")).thenReturn(Optional.of(oldSlot));

        service.modifyAppointmentByUser("A1", "Ahmad", "S1");

        assertEquals(oldSlot, appointment.getTimeSlot());
        verify(observer, never()).update(any(), any());
    }

    // -----------------------------
    // ✅ CANCEL (USER)
    // -----------------------------

    @Test
    void cancelAppointmentByUser_success() {
        service.cancelAppointmentByUser("A1", "Ahmad");

        assertEquals(AppointmentStatus.CANCELLED, appointment.getStatus());
        assertEquals(0, oldSlot.getBookedCount());

        verify(observer).update(eq(appointment), eq(AppointmentEventType.CANCELLED));
    }

    @Test
    void cancelAppointmentByUser_wrongOwner() {
        assertThrows(BookingException.class,
                () -> service.cancelAppointmentByUser("A1", "Wrong"));
    }

    // -----------------------------
    // 👑 ADMIN ACTIONS
    // -----------------------------

    @Test
    void cancelReservationByAdmin_success() {
        // ✅ نعمل login حقيقي
        Administrator admin = new Administrator("admin", "1234");

        AdminRepository adminRepository = mock(AdminRepository.class);
        when(adminRepository.findByUsername("admin"))
                .thenReturn(Optional.of(admin));

        authService = new AuthService(adminRepository, sessionManager);
        service = new ReservationManagementService(
                appointmentRepository,
                timeSlotRepository,
                List.of(rule),
                authService
        );
        service.registerObserver(observer);

        authService.login("admin", "1234");

        service.cancelReservationByAdmin("A1");

        assertEquals(AppointmentStatus.CANCELLED, appointment.getStatus());
        verify(observer).update(eq(appointment), eq(AppointmentEventType.CANCELLED));
    }

    @Test
    void cancelReservationByAdmin_notAuthenticated() {
        assertThrows(AuthorizationException.class,
                () -> service.cancelReservationByAdmin("A1"));
    }

    @Test
    void getAllReservationsByAdmin_success() {
        Administrator admin = new Administrator("admin", "1234");

        AdminRepository adminRepository = mock(AdminRepository.class);
        when(adminRepository.findByUsername("admin"))
                .thenReturn(Optional.of(admin));

        authService = new AuthService(adminRepository, sessionManager);
        service = new ReservationManagementService(
                appointmentRepository,
                timeSlotRepository,
                List.of(rule),
                authService
        );

        authService.login("admin", "1234");

        when(appointmentRepository.findAll()).thenReturn(List.of(appointment));

        List<Appointment> result = service.getAllReservationsByAdmin();

        assertEquals(1, result.size());
    }

    @Test
    void getAllReservationsByAdmin_notAuthenticated() {
        assertThrows(AuthorizationException.class,
                () -> service.getAllReservationsByAdmin());
    }

    // -----------------------------
    // ⏳ FUTURE / STATUS GUARDS
    // -----------------------------

    @Test
    void modifyAppointment_pastAppointment() {
        TimeSlot pastSlot = new TimeSlot(
                "P1",
                LocalDate.now().minusDays(1),
                LocalTime.of(10, 0),
                LocalTime.of(11, 0),
                10,
                0
        );

        Appointment pastAppointment = new Appointment(
                "A2",
                "Ahmad",
                pastSlot,
                2,
                AppointmentStatus.CONFIRMED
        );

        when(appointmentRepository.findById("A2"))
                .thenReturn(Optional.of(pastAppointment));

        assertThrows(BookingException.class,
                () -> service.modifyAppointmentByUser("A2", "Ahmad", "S2"));
    }

    @Test
    void modifyAppointment_cancelledAppointment() {
        appointment.cancel();

        assertThrows(BookingException.class,
                () -> service.modifyAppointmentByUser("A1", "Ahmad", "S2"));
    }
}