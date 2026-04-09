package com.appointmentsystem.presentation;

import com.appointmentsystem.domain.Appointment;
import com.appointmentsystem.domain.TimeSlot;
import com.appointmentsystem.exception.AuthenticationException;
import com.appointmentsystem.exception.AuthorizationException;
import com.appointmentsystem.exception.BookingException;
import com.appointmentsystem.AuthService;
import com.appointmentsystem.BookingService;
import com.appointmentsystem.ReservationManagementService;
import com.appointmentsystem.ScheduleService;

import java.util.List;
import java.util.Scanner;

/**
 * Console-based user interface for the appointment scheduling system.
 *
 * @author Mohammad
 * @version 3.1
 */
public class ConsoleApp {

    private final AuthService authService;
    private final ScheduleService scheduleService;
    private final BookingService bookingService;
    private final ReservationManagementService reservationManagementService;

    /** 🔥 تم تعديلها: لم نعد ننشئ Scanner داخليًا */
    private final Scanner scanner;

    /**
     * ✅ Constructor للتطبيق الحقيقي
     */
    public ConsoleApp(AuthService authService,
                      ScheduleService scheduleService,
                      BookingService bookingService,
                      ReservationManagementService reservationManagementService) {
        this(
                authService,
                scheduleService,
                bookingService,
                reservationManagementService,
                new Scanner(System.in) // يستخدم في التشغيل العادي
        );
    }

    /**
     * ✅ Constructor للتست (نمرر Scanner)
     */
    public ConsoleApp(AuthService authService,
                      ScheduleService scheduleService,
                      BookingService bookingService,
                      ReservationManagementService reservationManagementService,
                      Scanner scanner) {
        this.authService = authService;
        this.scheduleService = scheduleService;
        this.bookingService = bookingService;
        this.reservationManagementService = reservationManagementService;
        this.scanner = scanner;
    }

    /**
     * Starts the application loop.
     */
    public void start() {
        boolean running = true;

        while (running) {
            printMainMenu();

            if (!scanner.hasNextLine()) break; // 🔥 مهم للتست

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    openUserMenu();
                    break;
                case "2":
                    handleAdminLogin();
                    break;
                case "3":
                    running = false;
                    System.out.println("Exiting system...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }

            System.out.println();
        }
    }

    private void printMainMenu() {
        System.out.println("===== Appointment Scheduling System =====");
        System.out.println("1. User Menu");
        System.out.println("2. Administrator Login");
        System.out.println("3. Exit");
        System.out.print("Choose an option: ");
    }

    private void openUserMenu() {
        boolean inUserMenu = true;

        while (inUserMenu) {
            printUserMenu();

            if (!scanner.hasNextLine()) break;

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    handleViewAvailableSlots();
                    break;
                case "2":
                    handleBookAppointment();
                    break;
                case "3":
                    openManageMyAppointmentMenu();
                    break;
                case "4":
                    inUserMenu = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }

            System.out.println();
        }
    }

    private void printUserMenu() {
        System.out.println("===== User Menu =====");
        System.out.println("1. View Available Appointment Slots");
        System.out.println("2. Book Appointment");
        System.out.println("3. Manage My Appointment");
        System.out.println("4. Back");
        System.out.print("Choose an option: ");
    }

    private void openManageMyAppointmentMenu() {
        boolean inManageMenu = true;

        while (inManageMenu) {
            printManageMyAppointmentMenu();

            if (!scanner.hasNextLine()) break;

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    handleModifyMyAppointment();
                    break;
                case "2":
                    handleCancelMyAppointment();
                    break;
                case "3":
                    inManageMenu = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }

            System.out.println();
        }
    }

    private void printManageMyAppointmentMenu() {
        System.out.println("===== Manage My Appointment =====");
        System.out.println("1. Modify My Appointment");
        System.out.println("2. Cancel My Appointment");
        System.out.println("3. Back");
        System.out.print("Choose an option: ");
    }

    private void handleAdminLogin() {
        if (authService.isAuthenticated()) {
            System.out.println("Administrator already logged in: " + authService.getLoggedInUsername());
            openAdminMenu();
            return;
        }

        System.out.print("Enter administrator username: ");
        if (!scanner.hasNextLine()) return;
        String username = scanner.nextLine();

        System.out.print("Enter administrator password: ");
        if (!scanner.hasNextLine()) return;
        String password = scanner.nextLine();

        try {
            authService.login(username, password);
            System.out.println("Administrator login successful.");
            System.out.println();
            openAdminMenu();
        } catch (AuthenticationException e) {
            System.out.println("Login failed: " + e.getMessage());
        }
    }

    private void openAdminMenu() {
        boolean inAdminMenu = true;

        while (inAdminMenu && authService.isAuthenticated()) {
            printAdminMenu();

            if (!scanner.hasNextLine()) break;

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    handleViewAvailableSlots();
                    break;
                case "2":
                    handleViewAllReservations();
                    break;
                case "3":
                    handleModifyReservationByAdmin();
                    break;
                case "4":
                    handleCancelReservationByAdmin();
                    break;
                case "5":
                    handleAdminLogout();
                    inAdminMenu = false;
                    break;
                case "6":
                    inAdminMenu = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }

            System.out.println();
        }
    }

    private void printAdminMenu() {
        System.out.println("===== Administrator Menu =====");
        System.out.println("Logged in as: " + authService.getLoggedInUsername());
        System.out.println("1. View Available Appointment Slots");
        System.out.println("2. View All Reservations");
        System.out.println("3. Modify Reservation");
        System.out.println("4. Cancel Reservation");
        System.out.println("5. Logout");
        System.out.println("6. Back");
        System.out.print("Choose an option: ");
    }

    private void handleAdminLogout() {
        if (!authService.isAuthenticated()) {
            System.out.println("No administrator is currently logged in.");
            return;
        }

        authService.logout();
        System.out.println("Administrator logout successful.");
    }

    private void handleViewAvailableSlots() {
        List<TimeSlot> availableSlots = scheduleService.getAvailableSlots();

        if (availableSlots.isEmpty()) {
            System.out.println("No available appointment slots found.");
            return;
        }

        System.out.println("Available Appointment Slots:");
        for (TimeSlot slot : availableSlots) {
            System.out.println(slot);
        }
    }

    private void handleBookAppointment() {
        handleViewAvailableSlots();

        System.out.print("Enter your name: ");
        if (!scanner.hasNextLine()) return;
        String customerName = scanner.nextLine();

        System.out.print("Enter slot ID: ");
        if (!scanner.hasNextLine()) return;
        String slotId = scanner.nextLine();

        System.out.print("Enter number of participants: ");
        if (!scanner.hasNextLine()) return;
        String participantInput = scanner.nextLine();

        try {
            int participantCount = Integer.parseInt(participantInput);

            Appointment appointment = bookingService.bookAppointment(
                    customerName,
                    slotId,
                    participantCount
            );

            System.out.println("Appointment booked successfully.");
            System.out.println("Appointment ID: " + appointment.getId());
            System.out.println("Status: " + appointment.getStatus());
        } catch (NumberFormatException e) {
            System.out.println("Booking rejected: participant count must be a number.");
        } catch (BookingException e) {
            System.out.println(e.getMessage());
        }
    }

    private void handleModifyMyAppointment() {
        System.out.print("Enter your appointment ID: ");
        if (!scanner.hasNextLine()) return;
        String appointmentId = scanner.nextLine();

        System.out.print("Enter your name: ");
        if (!scanner.hasNextLine()) return;
        String customerName = scanner.nextLine();

        handleViewAvailableSlots();
        System.out.print("Enter new slot ID: ");
        if (!scanner.hasNextLine()) return;
        String newSlotId = scanner.nextLine();

        try {
            Appointment appointment = reservationManagementService.modifyAppointmentByUser(
                    appointmentId,
                    customerName,
                    newSlotId
            );

            System.out.println("Appointment modified successfully.");
            System.out.println(appointment);
        } catch (BookingException e) {
            System.out.println(e.getMessage());
        }
    }

    private void handleCancelMyAppointment() {
        System.out.print("Enter your appointment ID: ");
        if (!scanner.hasNextLine()) return;
        String appointmentId = scanner.nextLine();

        System.out.print("Enter your name: ");
        if (!scanner.hasNextLine()) return;
        String customerName = scanner.nextLine();

        try {
            Appointment appointment = reservationManagementService.cancelAppointmentByUser(
                    appointmentId,
                    customerName
            );

            System.out.println("Appointment cancelled successfully.");
            System.out.println(appointment);
        } catch (BookingException e) {
            System.out.println(e.getMessage());
        }
    }

    private void handleViewAllReservations() {
        try {
            List<Appointment> appointments = reservationManagementService.getAllReservationsByAdmin();

            if (appointments.isEmpty()) {
                System.out.println("No reservations found.");
                return;
            }

            System.out.println("All Reservations:");
            for (Appointment appointment : appointments) {
                System.out.println(appointment);
            }
        } catch (AuthorizationException e) {
            System.out.println(e.getMessage());
        }
    }

    private void handleModifyReservationByAdmin() {
        System.out.print("Enter appointment ID to modify: ");
        if (!scanner.hasNextLine()) return;
        String appointmentId = scanner.nextLine();

        handleViewAvailableSlots();
        System.out.print("Enter new slot ID: ");
        if (!scanner.hasNextLine()) return;
        String newSlotId = scanner.nextLine();

        try {
            Appointment appointment = reservationManagementService.modifyReservationByAdmin(
                    appointmentId,
                    newSlotId
            );

            System.out.println("Reservation modified successfully.");
            System.out.println(appointment);
        } catch (AuthorizationException | BookingException e) {
            System.out.println(e.getMessage());
        }
    }

    private void handleCancelReservationByAdmin() {
        System.out.print("Enter appointment ID to cancel: ");
        if (!scanner.hasNextLine()) return;
        String appointmentId = scanner.nextLine();

        try {
            Appointment appointment = reservationManagementService.cancelReservationByAdmin(appointmentId);
            System.out.println("Reservation cancelled successfully.");
            System.out.println(appointment);
        } catch (AuthorizationException | BookingException e) {
            System.out.println(e.getMessage());
        }
    }
}