package com.appointmentsystem.service;

import com.appointmentsystem.NotificationGateway;

import java.util.ArrayList;
import java.util.List;

/**
 * Test notification service used to capture reminder messages.
 *
 * @author Mohammad
 * @version 1.0
 */
public class TestNotificationService implements NotificationGateway {

    private final List<String> messages = new ArrayList<>();

    @Override
    public void send(String recipient, String message) {
        messages.add(recipient + " -> " + message);
    }

    public List<String> getMessages() {
        return messages;
    }
}
