package com.appointmentsystem;

/**
 * Abstraction for sending notifications.
 *
 * @author Mohammad
 * @version 1.0
 */
public interface NotificationGateway {
    void send(String recipient, String message);
}
