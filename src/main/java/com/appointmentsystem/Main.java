package com.appointmentsystem;

import com.appointmentsystem.persistence.AdminRepository;
import com.appointmentsystem.persistence.AppointmentRepository;
import com.appointmentsystem.persistence.TimeSlotRepository;
import com.appointmentsystem.persistence.inmemory.InMemoryAdminRepository;
import com.appointmentsystem.persistence.inmemory.InMemoryAppointmentRepository;
import com.appointmentsystem.persistence.inmemory.InMemoryTimeSlotRepository;
import com.appointmentsystem.presentation.ConsoleApp;
import com.appointmentsystem.security.SessionManager;
import com.appointmentsystem.service.AuthService;
import com.appointmentsystem.service.BookingService;
import com.appointmentsystem.service.NotificationService;
import com.appointmentsystem.service.ReservationManagementService;
import com.appointmentsystem.service.ScheduleService;
import com.appointmentsystem.strategy.BookingRuleStrategy;
import com.appointmentsystem.strategy.DurationRuleStrategy;
import com.appointmentsystem.strategy.ParticipantLimitRuleStrategy;

import java.util.Arrays;
import java.util.List;

/**
 * Application entry point.
 *
 * @author Mohammad
 * @version 3.0
 */
public class Main {

    /**
     * Starts the appointment scheduling system.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        AdminRepository adminRepository = new InMemoryAdminRepository();
        AppointmentRepository appointmentRepository = new InMemoryAppointmentRepository();
        TimeSlotRepository timeSlotRepository = new InMemoryTimeSlotRepository();

        SessionManager sessionManager = new SessionManager();
        AuthService authService = new AuthService(adminRepository, sessionManager);
        ScheduleService scheduleService = new ScheduleService(timeSlotRepository);

        List<BookingRuleStrategy> bookingRules = Arrays.asList(
                new DurationRuleStrategy(120),
                new ParticipantLimitRuleStrategy()
        );

        BookingService bookingService = new BookingService(
                timeSlotRepository,
                appointmentRepository,
                bookingRules
        );

        ReservationManagementService reservationManagementService =
                new ReservationManagementService(
                        appointmentRepository,
                        timeSlotRepository,
                        bookingRules,
                        authService
                );

        // --- Observer pattern wiring ---
        // One shared NotificationService instance observes both services.
        NotificationService notificationService = new NotificationService();
        bookingService.registerObserver(notificationService);
        reservationManagementService.registerObserver(notificationService);

        ConsoleApp consoleApp = new ConsoleApp(
                authService,
                scheduleService,
                bookingService,
                reservationManagementService
        );

        consoleApp.start();
    }
}