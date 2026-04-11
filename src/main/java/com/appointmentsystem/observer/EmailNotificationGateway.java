package com.appointmentsystem.observer;

import com.appointmentsystem.NotificationGateway;

public class EmailNotificationGateway implements NotificationGateway {
    @Override
    public void send(String email, String message) {
        System.out.println("[EMAIL SENT]");
        System.out.println("To: " + email);
        System.out.println("Message: " + message);
    }
}
