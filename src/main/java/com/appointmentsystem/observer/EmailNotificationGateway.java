package com.appointmentsystem.observer;

import com.appointmentsystem.NotificationGateway;

import java.util.logging.Logger;

public class EmailNotificationGateway implements NotificationGateway {
    private static final Logger LOGGER = Logger.getLogger(EmailNotificationGateway.class.getName());

    @Override
    public void send(String email, String message) {
        LOGGER.info("[EMAIL SENT]");
        LOGGER.info(() -> "To: " + email);
        LOGGER.info(() -> "Message: " + message);
    }
}
