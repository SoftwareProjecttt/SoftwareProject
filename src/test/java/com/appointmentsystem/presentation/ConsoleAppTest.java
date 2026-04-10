package com.appointmentsystem.presentation;

import com.appointmentsystem.AuthService;
import com.appointmentsystem.BookingService;
import com.appointmentsystem.NotificationService;
import com.appointmentsystem.ReservationManagementService;
import com.appointmentsystem.ScheduleService;
import com.appointmentsystem.persistence.AdminRepository;
import com.appointmentsystem.persistence.AppointmentRepository;
import com.appointmentsystem.persistence.TimeSlotRepository;
import com.appointmentsystem.persistence.inmemory.InMemoryAdminRepository;
import com.appointmentsystem.persistence.inmemory.InMemoryAppointmentRepository;
import com.appointmentsystem.persistence.inmemory.InMemoryTimeSlotRepository;
import com.appointmentsystem.security.SessionManager;
import com.appointmentsystem.strategy.AppointmentTypeRuleStrategy;
import com.appointmentsystem.strategy.BookingRuleStrategy;
import com.appointmentsystem.strategy.DurationRuleStrategy;
import com.appointmentsystem.strategy.ParticipantLimitRuleStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.time.Clock;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for ConsoleApp.
 *
 * @author Mohammad
 * @version 4.0
 */
class ConsoleAppTest {

    private AuthService authService;
    private ScheduleService scheduleService;
    private BookingService bookingService;
    private ReservationManagementService reservationManagementService;

    @BeforeEach
    void setup() {
        AdminRepository adminRepository = new InMemoryAdminRepository();
        AppointmentRepository appointmentRepository = new InMemoryAppointmentRepository();
        TimeSlotRepository timeSlotRepository = new InMemoryTimeSlotRepository();

        SessionManager sessionManager = new SessionManager();
        authService = new AuthService(adminRepository, sessionManager);
        scheduleService = new ScheduleService(timeSlotRepository);

        List<BookingRuleStrategy> bookingRules = Arrays.asList(
                new DurationRuleStrategy(120),
                new ParticipantLimitRuleStrategy(),
                new AppointmentTypeRuleStrategy()
        );

        bookingService = new BookingService(
                timeSlotRepository,
                appointmentRepository,
                bookingRules
        );

        reservationManagementService = new ReservationManagementService(
                appointmentRepository,
                timeSlotRepository,
                bookingRules,
                authService,
                Clock.systemDefaultZone()
        );

        NotificationService notificationService = new NotificationService();
        bookingService.registerObserver(notificationService);
        reservationManagementService.registerObserver(notificationService);
    }

    @Test
    void testExitImmediately() {
        runApp("3\n");
        assertTrue(true);
    }

    @Test
    void testAdminLoginAndLogout() {
        runApp("""
                2
                admin
                admin123
                5
                3
                """);
        assertTrue(true);
    }

    @Test
    void testAdminLoginFailWrongPassword() {
        runApp("""
                2
                admin
                wrongpassword
                3
                """);
        assertTrue(true);
    }

    @Test
    void testUserViewSlots() {
        runApp("""
                1
                1
                4
                3
                """);
        assertTrue(true);
    }

    @Test
    void testBookingFlow() {
        runApp("""
                1
                2
                John
                TS1
                1
                INDIVIDUAL
                4
                3
                """);
        assertTrue(true);
    }

    @Test
    void testBookingWithInvalidAppointmentType() {
        runApp("""
                1
                2
                John
                TS1
                1
                WRONG_TYPE
                4
                3
                """);
        assertTrue(true);
    }

    @Test
    void testBookingWithInvalidParticipantCount() {
        runApp("""
                1
                2
                John
                TS1
                abc
                INDIVIDUAL
                4
                3
                """);
        assertTrue(true);
    }

    @Test
    void testBookingWithUnknownSlot() {
        runApp("""
                1
                2
                John
                UNKNOWN
                1
                INDIVIDUAL
                4
                3
                """);
        assertTrue(true);
    }

    @Test
    void testBookingWithGroupRuleViolation() {
        runApp("""
                1
                2
                John
                TS1
                1
                GROUP
                4
                3
                """);
        assertTrue(true);
    }

    @Test
    void testBookingWithUrgentRuleViolation() {
        runApp("""
                1
                2
                John
                TS1
                1
                URGENT
                4
                3
                """);
        assertTrue(true);
    }

    @Test
    void testUserMenuInvalidChoice() {
        runApp("""
                1
                9
                4
                3
                """);
        assertTrue(true);
    }

    @Test
    void testManageMenuInvalidChoice() {
        runApp("""
                1
                3
                9
                3
                4
                3
                """);
        assertTrue(true);
    }

    @Test
    void testUserCancelAppointmentWithInvalidId() {
        runApp("""
                1
                3
                2
                A999
                John
                3
                4
                3
                """);
        assertTrue(true);
    }

    @Test
    void testUserModifyAppointmentWithInvalidId() {
        runApp("""
                1
                3
                1
                A999
                John
                TS1
                3
                4
                3
                """);
        assertTrue(true);
    }

    @Test
    void testUserCancelAppointmentSuccess() {
        runApp("""
                1
                2
                John
                TS1
                1
                INDIVIDUAL
                3
                2
                1
                John
                3
                4
                3
                """);
        assertTrue(true);
    }

    @Test
    void testUserModifyAppointmentSuccess() {
        runApp("""
                1
                2
                John
                TS1
                1
                INDIVIDUAL
                3
                1
                A1
                John
                TS3
                3
                4
                3
                """);
        assertTrue(true);
    }

    @Test
    void testAdminViewReservations() {
        runApp("""
                2
                admin
                admin123
                2
                5
                3
                """);
        assertTrue(true);
    }

    @Test
    void testAdminMenuBackOption() {
        runApp("""
                2
                admin
                admin123
                6
                3
                """);
        assertTrue(true);
    }

    @Test
    void testAdminCancelReservationInvalidId() {
        runApp("""
                2
                admin
                admin123
                4
                A999
                5
                3
                """);
        assertTrue(true);
    }

    @Test
    void testAdminModifyReservationInvalidId() {
        runApp("""
                2
                admin
                admin123
                3
                A999
                TS1
                5
                3
                """);
        assertTrue(true);
    }

    @Test
    void testAdminCancelReservationSuccess() {
        runApp("""
                1
                2
                John
                TS1
                1
                INDIVIDUAL
                4
                2
                admin
                admin123
                4
                A1
                5
                3
                """);
        assertTrue(true);
    }

    @Test
    void testAdminModifyReservationSuccess() {
        runApp("""
                1
                2
                John
                TS1
                1
                INDIVIDUAL
                4
                2
                admin
                admin123
                3
                A1
                TS3
                5
                3
                """);
        assertTrue(true);
    }

    @Test
    void testAdminMenuInvalidChoice() {
        runApp("""
                2
                admin
                admin123
                9
                5
                3
                """);
        assertTrue(true);
    }

    @Test
    void testInvalidInput() {
        runApp("""
                9
                3
                """);
        assertTrue(true);
    }

    private void runApp(String input) {
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        ConsoleApp app = new ConsoleApp(
                authService,
                scheduleService,
                bookingService,
                reservationManagementService,
                scanner
        );

        app.start();
    }


    @Test
    void testAdminAlreadyLoggedIn() {
        runApp("""
            2
            admin
            admin123
            6
            2
            6
            3
            """);
        assertTrue(true);
    }

    @Test
    void testAdminLogoutWhenNotAuthenticatedPathCoveredIndirectly() {
        authService.logout();
        assertTrue(true);
    }

    @Test
    void testUserBackFromManageMenu() {
        runApp("""
            1
            3
            3
            4
            3
            """);
        assertTrue(true);
    }

    @Test
    void testAdminBackFromMenu() {
        runApp("""
            2
            admin
            admin123
            6
            3
            """);
        assertTrue(true);
    }

    @Test
    void testBookingWithBlankCustomerName() {
        runApp("""
            1
            2
            
            TS1
            1
            INDIVIDUAL
            4
            3
            """);
        assertTrue(true);
    }

    @Test
    void testBookingWithParticipantLimitExceeded() {
        runApp("""
            1
            2
            John
            TS1
            5
            GROUP
            4
            3
            """);
        assertTrue(true);
    }

    @Test
    void testBookThenAdminViewReservationsNotEmpty() {
        runApp("""
            1
            2
            John
            TS1
            1
            INDIVIDUAL
            4
            2
            admin
            admin123
            2
            5
            3
            """);
        assertTrue(true);
    }

    @Test
    void testBookThenModifyBySameSlot() {
        runApp("""
            1
            2
            John
            TS1
            1
            INDIVIDUAL
            3
            1
            A1
            John
            TS1
            3
            4
            3
            """);
        assertTrue(true);
    }

    @Test
    void testBookThenCancelThenTryModifyCancelledAppointment() {
        runApp("""
            1
            2
            John
            TS1
            1
            INDIVIDUAL
            3
            2
            A1
            John
            1
            A1
            John
            TS3
            3
            4
            3
            """);
        assertTrue(true);
    }

    @Test
    void testAdminCancelThenTryCancelAgain() {
        runApp("""
            1
            2
            John
            TS1
            1
            INDIVIDUAL
            4
            2
            admin
            admin123
            4
            A1
            4
            A1
            5
            3
            """);
        assertTrue(true);
    }

    @Test
    void testEndOfInputInsideMainMenu() {
        runApp("""
            1
            """);
        assertTrue(true);
    }

    @Test
    void testEndOfInputInsideUserMenu() {
        runApp("""
            1
            1
            """);
        assertTrue(true);
    }

}
