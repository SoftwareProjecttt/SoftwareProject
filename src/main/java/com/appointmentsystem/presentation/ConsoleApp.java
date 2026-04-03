package com.appointmentsystem.presentation;

import com.appointmentsystem.domain.Appointment;
import com.appointmentsystem.domain.TimeSlot;
import com.appointmentsystem.exception.AuthenticationException;
import com.appointmentsystem.exception.AuthorizationException;
import com.appointmentsystem.exception.BookingException;
import com.appointmentsystem.service.AuthService;
import com.appointmentsystem.service.BookingService;
import com.appointmentsystem.service.ReservationManagementService;
import com.appointmentsystem.service.ScheduleService;

import java.util.List;
import java.util.Scanner;

/**
 * Console-based user interface for the appointment scheduling system.
 *
 * @author Mohammad
 * @version 3.0
 */
public class ConsoleApp {

    /** Authentication service. */
    private final AuthService authService;

    /** Schedule service. */
    private final ScheduleService scheduleService;

    /** Booking service. */
    private final BookingService bookingService;

    /** Reservation management service. */
    private final ReservationManagementService reservationManagementService;

    /** Console scanner. */
    private final Scanner scanner;

    /**
     * Creates a new console application.
     *
     * @param authService authentication service
     * @param scheduleService schedule service
     * @param bookingService booking service
     * @param reservationManagementService reservation management service
     */
    public ConsoleApp(AuthService authService,
                      ScheduleService scheduleService,
                      BookingService bookingService,
                      ReservationManagementService reservationManagementService) {
        this.authService = authService;
        this.scheduleService = scheduleService;
        this.bookingService = bookingService;
        this.reservationManagementService = reservationManagementService;
        this.scanner = new Scanner(System.in);
    }

    /**
     * Starts the application loop.
     */
    public void start() {
        boolean running = true;

        while (running) {
            printMainMenu();
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

    /**
     * Prints the main menu.
     */
    private void printMainMenu() {
        System.out.println("===== Appointment Scheduling System =====");
        System.out.println("1. User Menu");
        System.out.println("2. Administrator Login");
        System.out.println("3. Exit");
        System.out.print("Choose an option: ");
    }

    /**
     * Opens the user menu.
     */
    private void openUserMenu() {
        boolean inUserMenu = true;

        while (inUserMenu) {
            printUserMenu();
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

    /**
     * Prints the user menu.
     */
    private void printUserMenu() {
        System.out.println("===== User Menu =====");
        System.out.println("1. View Available Appointment Slots");
        System.out.println("2. Book Appointment");
        System.out.println("3. Manage My Appointment");
        System.out.println("4. Back");
        System.out.print("Choose an option: ");
    }

    /**
     * Opens the user's appointment management menu.
     */
    private void openManageMyAppointmentMenu() {
        boolean inManageMenu = true;

        while (inManageMenu) {
            printManageMyAppointmentMenu();
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

    /**
     * Prints the manage-my-appointment menu.
     */
    private void printManageMyAppointmentMenu() {
        System.out.println("===== Manage My Appointment =====");
        System.out.println("1. Modify My Appointment");
        System.out.println("2. Cancel My Appointment");
        System.out.println("3. Back");
        System.out.print("Choose an option: ");
    }

    /**
     * Handles administrator login.
     */
    private void handleAdminLogin() {
        if (authService.isAuthenticated()) {
            System.out.println("Administrator already logged in: " + authService.getLoggedInUsername());
            openAdminMenu();
            return;
        }

        System.out.print("Enter administrator username: ");
        String username = scanner.nextLine();

        System.out.print("Enter administrator password: ");
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

    /**
     * Opens the administrator menu.
     */
    private void openAdminMenu() {
        boolean inAdminMenu = true;

        while (inAdminMenu && authService.isAuthenticated()) {
            printAdminMenu();
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

    /**
     * Prints the administrator menu.
     */
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

    /**
     * Handles administrator logout.
     */
    private void handleAdminLogout() {
        if (!authService.isAuthenticated()) {
            System.out.println("No administrator is currently logged in.");
            return;
        }

        authService.logout();
        System.out.println("Administrator logout successful.");
    }

    /**
     * Displays available slots.
     */
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

    /**
     * Handles booking a new appointment.
     */
    private void handleBookAppointment() {
        handleViewAvailableSlots();

        System.out.print("Enter your name: ");
        String customerName = scanner.nextLine();

        System.out.print("Enter slot ID: ");
        String slotId = scanner.nextLine();

        System.out.print("Enter number of participants: ");
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

    /**
     * Handles modifying the user's own appointment.
     */
    private void handleModifyMyAppointment() {
        System.out.print("Enter your appointment ID: ");
        String appointmentId = scanner.nextLine();

        System.out.print("Enter your name: ");
        String customerName = scanner.nextLine();

        handleViewAvailableSlots();
        System.out.print("Enter new slot ID: ");
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

    /**
     * Handles cancelling the user's own appointment.
     */
    private void handleCancelMyAppointment() {
        System.out.print("Enter your appointment ID: ");
        String appointmentId = scanner.nextLine();

        System.out.print("Enter your name: ");
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

    /**
     * Displays all reservations for administrators.
     */
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

    /**
     * Handles reservation modification by an administrator.
     */
    private void handleModifyReservationByAdmin() {
        System.out.print("Enter appointment ID to modify: ");
        String appointmentId = scanner.nextLine();

        handleViewAvailableSlots();
        System.out.print("Enter new slot ID: ");
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

    /**
     * Handles reservation cancellation by an administrator.
     */
    private void handleCancelReservationByAdmin() {
        System.out.print("Enter appointment ID to cancel: ");
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