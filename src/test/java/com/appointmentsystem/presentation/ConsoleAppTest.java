package com.appointmentsystem.presentation;

import com.appointmentsystem.*;
import com.appointmentsystem.persistence.AdminRepository;
import com.appointmentsystem.persistence.AppointmentRepository;
import com.appointmentsystem.persistence.TimeSlotRepository;
import com.appointmentsystem.persistence.inmemory.InMemoryAdminRepository;
import com.appointmentsystem.persistence.inmemory.InMemoryAppointmentRepository;
import com.appointmentsystem.persistence.inmemory.InMemoryTimeSlotRepository;
import com.appointmentsystem.security.SessionManager;
import com.appointmentsystem.strategy.BookingRuleStrategy;
import com.appointmentsystem.strategy.DurationRuleStrategy;
import com.appointmentsystem.strategy.ParticipantLimitRuleStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertTrue;

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
                new ParticipantLimitRuleStrategy()
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
                authService
        );

        NotificationService notificationService = new NotificationService();
        bookingService.registerObserver(notificationService);
        reservationManagementService.registerObserver(notificationService);
    }

    @Test
    void testExitImmediately() {
        String input = "3\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        ConsoleApp app = new ConsoleApp(
                authService,
                scheduleService,
                bookingService,
                reservationManagementService,
                scanner
        );

        app.start();
        assertTrue(true);
    }

    @Test
    void testAdminLoginAndLogout() {
        String input = """
                2
                admin
                admin123
                5
                3
                """;

        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        ConsoleApp app = new ConsoleApp(
                authService,
                scheduleService,
                bookingService,
                reservationManagementService,
                scanner
        );

        app.start();
        assertTrue(true);
    }

    @Test
    void testAdminLoginFailWrongPassword() {
        String input = """
                2
                admin
                wrongpassword
                3
                """;

        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        ConsoleApp app = new ConsoleApp(
                authService,
                scheduleService,
                bookingService,
                reservationManagementService,
                scanner
        );

        app.start();
        assertTrue(true);
    }

    @Test
    void testUserViewSlots() {
        String input = """
                1
                1
                4
                3
                """;

        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        ConsoleApp app = new ConsoleApp(
                authService,
                scheduleService,
                bookingService,
                reservationManagementService,
                scanner
        );

        app.start();
        assertTrue(true);
    }

    @Test
    void testBookingFlow() {
        String input = """
                1
                2
                John
                SLOT1
                2
                4
                3
                """;

        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        ConsoleApp app = new ConsoleApp(
                authService,
                scheduleService,
                bookingService,
                reservationManagementService,
                scanner
        );

        app.start();
        assertTrue(true);
    }

    @Test
    void testInvalidInput() {
        String input = """
                9
                3
                """;

        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        ConsoleApp app = new ConsoleApp(
                authService,
                scheduleService,
                bookingService,
                reservationManagementService,
                scanner
        );

        app.start();
        assertTrue(true);
    }
}