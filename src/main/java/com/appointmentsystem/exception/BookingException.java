package com.appointmentsystem.exception;

/**
 * Thrown when booking validation fails.
 *
 * @author Mohammad
 * @version 1.0
 */
public class BookingException extends RuntimeException {

    /**
     * Creates a new booking exception.
     *
     * @param message error message
     */
    public BookingException(String message) {
        super(message);
    }
}