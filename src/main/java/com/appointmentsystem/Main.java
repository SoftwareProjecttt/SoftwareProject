package com.appointmentsystem;

import com.appointmentsystem.persistence.AdminRepository;
import com.appointmentsystem.persistence.AppointmentRepository;
import com.appointmentsystem.persistence.TimeSlotRepository;
import com.appointmentsystem.persistence.inmemory.InMemoryAdminRepository;
import com.appointmentsystem.persistence.inmemory.InMemoryAppointmentRepository;
import com.appointmentsystem.persistence.inmemory.InMemoryTimeSlotRepository;
import com.appointmentsystem.presentation.ConsoleApp;
import com.appointmentsystem.security.SessionManager;
import com.appointmentsystem.strategy.AppointmentTypeRuleStrategy;
import com.appointmentsystem.strategy.BookingRuleStrategy;
import com.appointmentsystem.strategy.DurationRuleStrategy;
import com.appointmentsystem.strategy.ParticipantLimitRuleStrategy;

import java.time.Clock;
import java.util.Arrays;
import java.util.List;

/**
 * Application entry point.
 *
 * @author Mohammad
 * @version 4.0
 */
public class Main {

    public static void main(String[] args) {
        AdminRepository adminRepository = new InMemoryAdminRepository();
        AppointmentRepository appointmentRepository = new InMemoryAppointmentRepository();
        TimeSlotRepository timeSlotRepository = new InMemoryTimeSlotRepository();

        SessionManager sessionManager = new SessionManager();
        AuthService authService = new AuthService(adminRepository, sessionManager);
        ScheduleService scheduleService = new ScheduleService(timeSlotRepository);

        List<BookingRuleStrategy> bookingRules = Arrays.asList(
                new DurationRuleStrategy(120),
                new ParticipantLimitRuleStrategy(),
                new AppointmentTypeRuleStrategy()
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
                        authService,
                        Clock.systemDefaultZone()
                );

        NotificationGateway emailGateway = new com.appointmentsystem.observer.EmailNotificationGateway();
        NotificationService notificationService = new NotificationService(emailGateway);
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
