package com.appointmentsystem.presentation;

import com.appointmentsystem.AuthService;
import com.appointmentsystem.BookingService;
import com.appointmentsystem.ReservationManagementService;
import com.appointmentsystem.ScheduleService;
import com.appointmentsystem.domain.Appointment;
import com.appointmentsystem.domain.AppointmentType;
import com.appointmentsystem.domain.TimeSlot;
import com.appointmentsystem.exception.AuthenticationException;
import com.appointmentsystem.exception.AuthorizationException;
import com.appointmentsystem.exception.BookingException;

import java.util.List;
import java.util.Scanner;

/**
 * Console-based user interface for the appointment scheduling system.
 * Main CLI Controller.
 */
public class ConsoleApp {

    private final AuthService authService;
    private final ScheduleService scheduleService;
    private final BookingService bookingService;
    private final ReservationManagementService reservationManagementService;
    private final Scanner scanner;

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
    
    private String safeReadLine() {
        if (!scanner.hasNextLine()) {
            return null;
        }
        return scanner.nextLine().trim();
    }

    public void start() {
        boolean exit = false;
        while (!exit) {
            printMenu();
            
            String choice = safeReadLine();
            if (choice == null) {
                break;
            }

            switch (choice) {
                case "1":
                    handleLogin();
                    break;
                case "2":
                    handleViewAvailableSlots();
                    break;
                case "3":
                    handleBookAppointment();
                    break;
                case "4":
                    handleModifyAppointment();
                    break;
                case "5":
                    handleCancelAppointment();
                    break;
                case "6":
                    handleViewAllAppointments();
                    break;
                case "7":
                    System.out.println("\nExiting system... Goodbye!");
                    exit = true;
                    break;
                default:
                    System.out.println("\n[ERROR] Invalid choice. Please try again.");
            }
        }
    }

    private void printMenu() {
        System.out.println("\n==================================================");
        System.out.println("## WELCOME TO APPOINTMENT SYSTEM");
        System.out.println("==================================================");
        System.out.println("\nChoose an option:\n");
        System.out.println("1. Login");
        System.out.println("2. View Available Slots");
        System.out.println("3. Book Appointment");
        System.out.println("4. Modify Appointment");
        System.out.println("5. Cancel Appointment");
        System.out.println("6. View All Appointments");
        System.out.println("7. Exit");
        System.out.println("\n==================================================");
        System.out.print("Enter choice: ");
    }

    private void handleLogin() {
        if (authService.isAuthenticated()) {
            System.out.println("\n[INFO] You are currently logged in as: " + authService.getLoggedInUsername());
            System.out.print("Do you want to logout? (y/n): ");
            String ans = safeReadLine();
            if (ans == null) return;
            
            if (ans.equalsIgnoreCase("y")) {
                authService.logout();
                System.out.println("[SUCCESS] Logged out successfully.");
            }
            return;
        }

        System.out.println("\n--- Admin Login ---");
        System.out.print("Enter username: ");
        String username = safeReadLine();
        if (username == null) return;
        
        System.out.print("Enter password: ");
        String password = safeReadLine();
        if (password == null) return;

        try {
            authService.login(username, password);
            System.out.println("\n[SUCCESS] Login successful! Welcome, " + username + ".");
        } catch (AuthenticationException e) {
            System.out.println("\n[ERROR] Login failed - " + e.getMessage());
        }
    }

    private void handleViewAvailableSlots() {
        List<TimeSlot> slots = scheduleService.getAvailableSlots();
        if (slots.isEmpty()) {
            System.out.println("\n[INFO] No available slots at the moment.");
            return;
        }
        System.out.println("\n--- Available Slots ---");
        for (TimeSlot slot : slots) {
            System.out.println(slot);
        }
        System.out.println("-----------------------");
    }

    private void handleBookAppointment() {
        if (!authService.isAuthenticated()) {
            System.out.println("\n[ERROR] You must be logged in as admin to perform this action.");
            return;
        }
        System.out.println("\n--- Book Appointment ---");
        handleViewAvailableSlots();
        
        System.out.print("\nEnter Customer Name: ");
        String name = safeReadLine();
        if (name == null) return;
        
        System.out.print("Enter Slot ID: ");
        String slotId = safeReadLine();
        if (slotId == null) return;
        
        System.out.print("Enter Participant Count: ");
        String countStr = safeReadLine();
        if (countStr == null) return;

        System.out.println("\nAvailable Appointment Types:");
        for (AppointmentType type : AppointmentType.values()) {
            System.out.println("- " + type.name());
        }
        System.out.print("Enter Appointment Type: ");
        String typeStr = safeReadLine();
        if (typeStr == null) return;

        try {
            int count = Integer.parseInt(countStr);
            AppointmentType type = AppointmentType.valueOf(typeStr.toUpperCase());
            
            Appointment appt = bookingService.bookAppointment(name, slotId, count, type);
            
            System.out.println("\n[SUCCESS] Appointment booked successfully!");
            System.out.println(appt);
        } catch (NumberFormatException e) {
            System.out.println("\n[ERROR] Participant count must be a valid number.");
        } catch (IllegalArgumentException e) {
            System.out.println("\n[ERROR] Invalid appointment type.");
        } catch (BookingException e) {
            System.out.println("\n[ERROR] " + e.getMessage());
        }
    }

    private void handleModifyAppointment() {
        if (!authService.isAuthenticated()) {
            System.out.println("\n[ERROR] You must be logged in as admin to perform this action.");
            return;
        }
        System.out.println("\n--- Modify Appointment ---");
        System.out.print("Enter Appointment ID to modify: ");
        String apptId = safeReadLine();
        if (apptId == null) return;
        
        System.out.println("\nFinding available slots...");
        handleViewAvailableSlots();
        
        System.out.print("\nEnter New Slot ID: ");
        String newSlotId = safeReadLine();
        if (newSlotId == null) return;
        
        try {
            Appointment appt = reservationManagementService.modifyReservationByAdmin(apptId, newSlotId);
            System.out.println("\n[SUCCESS] Appointment modified successfully!");
            System.out.println(appt);
        } catch (AuthorizationException | BookingException e) {
            System.out.println("\n[ERROR] " + e.getMessage());
        }
    }

    private void handleCancelAppointment() {
        if (!authService.isAuthenticated()) {
            System.out.println("\n[ERROR] You must be logged in as admin to perform this action.");
            return;
        }
        System.out.println("\n--- Cancel Appointment ---");
        System.out.print("Enter Appointment ID to cancel: ");
        String apptId = safeReadLine();
        if (apptId == null) return;
        
        try {
            Appointment appt = reservationManagementService.cancelReservationByAdmin(apptId);
            System.out.println("\n[SUCCESS] Appointment cancelled successfully!");
            System.out.println(appt);
        } catch (AuthorizationException | BookingException e) {
            System.out.println("\n[ERROR] " + e.getMessage());
        }
    }

    private void handleViewAllAppointments() {
        if (!authService.isAuthenticated()) {
            System.out.println("\n[ERROR] You must be logged in as admin to perform this action.");
            return;
        }
        System.out.println("\n--- All Appointments ---");
        try {
            List<Appointment> apps = reservationManagementService.getAllReservationsByAdmin();
            if (apps.isEmpty()) {
                System.out.println("[INFO] No appointments found.");
                return;
            }
            for (Appointment appt : apps) {
                System.out.println(appt);
            }
            System.out.println("------------------------");
        } catch (AuthorizationException e) {
             System.out.println("\n[ERROR] " + e.getMessage());
             System.out.println("Hint: You must be logged in as an admin to view all reservations.");
        }
    }
}
