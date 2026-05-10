package com.appointmentsystem.presentation;

import com.appointmentsystem.AuthService;
import com.appointmentsystem.BookingService;
import com.appointmentsystem.ReservationManagementService;
import com.appointmentsystem.ScheduleService;
import com.appointmentsystem.domain.Appointment;
import com.appointmentsystem.domain.AppointmentStatus;
import com.appointmentsystem.domain.AppointmentType;
import com.appointmentsystem.domain.TimeSlot;
import com.appointmentsystem.exception.AuthenticationException;
import com.appointmentsystem.exception.AuthorizationException;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class ConsoleAppTest {

    @Test
    void exitOption_printsGoodbyeMessage() {
        String output = runAppWithMocks("7\n", false, null, null, null, null);
        assertTrue(output.contains("Exiting system... Goodbye!"));
    }

    @Test
    void invalidChoice_printsError() {
        String output = runAppWithMocks("9\n7\n", false, null, null, null, null);
        assertTrue(output.contains("[ERROR] Invalid choice. Please try again."));
    }

    @Test
    void loginSuccess_callsAuthServiceAndPrintsMessage() {
        AuthService authService = mock(AuthService.class);
        when(authService.isAuthenticated()).thenReturn(false);

        String output = runAppWithMocks("1\nadmin\n1234\n7\n", false, authService, null, null, null);

        verify(authService).login("admin", "1234");
        assertTrue(output.contains("[SUCCESS] Login successful! Welcome, admin."));
    }

    @Test
    void loginFailure_printsAuthenticationError() {
        AuthService authService = mock(AuthService.class);
        when(authService.isAuthenticated()).thenReturn(false);
        doThrow(new AuthenticationException("Invalid password."))
                .when(authService).login("admin", "wrong");

        String output = runAppWithMocks("1\nadmin\nwrong\n7\n", false, authService, null, null, null);

        verify(authService).login("admin", "wrong");
        assertTrue(output.contains("[ERROR] Login failed - Invalid password."));
    }

    @Test
    void bookingWithoutAuthentication_blocksAction() {
        BookingService bookingService = mock(BookingService.class);

        String output = runAppWithMocks("3\n7\n", false, null, null, bookingService, null);

        verify(bookingService, never()).bookAppointment(anyString(), anyString(), anyInt(), any());
        assertTrue(output.contains("You must be logged in as admin to perform this action."));
    }

    @Test
    void bookingWithAuthentication_callsServiceAndPrintsSuccess() {
        BookingService bookingService = mock(BookingService.class);
        ScheduleService scheduleService = mock(ScheduleService.class);
        when(scheduleService.getAvailableSlots()).thenReturn(List.of(createTimeSlot("TS1")));
        when(bookingService.bookAppointment("Sara", "TS1", 1, AppointmentType.INDIVIDUAL))
                .thenReturn(createAppointment("A1", "Sara", "TS1"));

        String output = runAppWithMocks(
                "3\nSara\nTS1\n1\nINDIVIDUAL\n7\n",
                true,
                null,
                scheduleService,
                bookingService,
                null
        );

        verify(bookingService).bookAppointment("Sara", "TS1", 1, AppointmentType.INDIVIDUAL);
        assertTrue(output.contains("[SUCCESS] Appointment booked successfully!"));
        assertTrue(output.contains("Appointment ID: A1"));
    }

    @Test
    void bookingInvalidParticipantCount_showsValidationAndSkipsServiceCall() {
        BookingService bookingService = mock(BookingService.class);
        ScheduleService scheduleService = mock(ScheduleService.class);
        when(scheduleService.getAvailableSlots()).thenReturn(List.of(createTimeSlot("TS1")));

        String output = runAppWithMocks(
                "3\nSara\nTS1\nabc\nINDIVIDUAL\n7\n",
                true,
                null,
                scheduleService,
                bookingService,
                null
        );

        verify(bookingService, never()).bookAppointment(anyString(), anyString(), anyInt(), any());
        assertTrue(output.contains("Participant count must be a valid number."));
    }

    @Test
    void modifyWithAuthentication_callsReservationService() {
        ReservationManagementService reservationService = mock(ReservationManagementService.class);
        ScheduleService scheduleService = mock(ScheduleService.class);
        when(scheduleService.getAvailableSlots()).thenReturn(List.of(createTimeSlot("TS2")));
        when(reservationService.modifyReservationByAdmin("A1", "TS2"))
                .thenReturn(createAppointment("A1", "Sara", "TS2"));

        String output = runAppWithMocks(
                "4\nA1\nTS2\n7\n",
                true,
                null,
                scheduleService,
                null,
                reservationService
        );

        verify(reservationService).modifyReservationByAdmin("A1", "TS2");
        assertTrue(output.contains("[SUCCESS] Appointment modified successfully!"));
    }

    @Test
    void cancelWithAuthentication_callsReservationService() {
        ReservationManagementService reservationService = mock(ReservationManagementService.class);
        Appointment cancelled = createAppointment("A1", "Sara", "TS1");
        cancelled.cancel();
        when(reservationService.cancelReservationByAdmin("A1")).thenReturn(cancelled);

        String output = runAppWithMocks(
                "5\nA1\n7\n",
                true,
                null,
                null,
                null,
                reservationService
        );

        verify(reservationService).cancelReservationByAdmin("A1");
        assertTrue(output.contains("[SUCCESS] Appointment cancelled successfully!"));
    }

    @Test
    void viewAllAppointmentsEmpty_showsInfoMessage() {
        ReservationManagementService reservationService = mock(ReservationManagementService.class);
        when(reservationService.getAllReservationsByAdmin()).thenReturn(List.of());

        String output = runAppWithMocks("6\n7\n", true, null, null, null, reservationService);

        verify(reservationService).getAllReservationsByAdmin();
        assertTrue(output.contains("[INFO] No appointments found."));
    }

    @Test
    void viewAllAppointmentsAuthorizationError_showsHint() {
        ReservationManagementService reservationService = mock(ReservationManagementService.class);
        when(reservationService.getAllReservationsByAdmin())
                .thenThrow(new AuthorizationException("Access denied."));

        String output = runAppWithMocks("6\n7\n", true, null, null, null, reservationService);

        assertTrue(output.contains("[ERROR] Access denied."));
        assertTrue(output.contains("Hint: You must be logged in as an admin to view all reservations."));
    }

    private String runAppWithMocks(String input,
                                   boolean authenticated,
                                   AuthService authServiceOverride,
                                   ScheduleService scheduleServiceOverride,
                                   BookingService bookingServiceOverride,
                                   ReservationManagementService reservationServiceOverride) {
        InputStream originalIn = System.in;
        PrintStream originalOut = System.out;

        ByteArrayOutputStream outputBuffer = new ByteArrayOutputStream();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes());

        AuthService authService = authServiceOverride != null ? authServiceOverride : mock(AuthService.class);
        when(authService.isAuthenticated()).thenReturn(authenticated);
        if (authenticated) {
            when(authService.getLoggedInUsername()).thenReturn("admin");
        }

        ScheduleService scheduleService = scheduleServiceOverride != null
                ? scheduleServiceOverride
                : mock(ScheduleService.class);
        when(scheduleService.getAvailableSlots()).thenReturn(List.of());

        BookingService bookingService = bookingServiceOverride != null
                ? bookingServiceOverride
                : mock(BookingService.class);

        ReservationManagementService reservationService = reservationServiceOverride != null
                ? reservationServiceOverride
                : mock(ReservationManagementService.class);
        if (reservationServiceOverride == null) {
            when(reservationService.getAllReservationsByAdmin()).thenReturn(List.of());
        }

        try {
            System.setIn(inputStream);
            System.setOut(new PrintStream(outputBuffer));

            ConsoleApp app = new ConsoleApp(
                    authService,
                    scheduleService,
                    bookingService,
                    reservationService
            );
            app.start();
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }

        return outputBuffer.toString();
    }

    private TimeSlot createTimeSlot(String id) {
        return new TimeSlot(
                id,
                LocalDate.now().plusDays(1),
                LocalTime.of(10, 0),
                LocalTime.of(11, 0),
                5,
                0
        );
    }

    private Appointment createAppointment(String id, String customerName, String slotId) {
        return new Appointment(
                id,
                customerName,
                createTimeSlot(slotId),
                1,
                AppointmentStatus.CONFIRMED,
                AppointmentType.INDIVIDUAL
        );
    }
}
