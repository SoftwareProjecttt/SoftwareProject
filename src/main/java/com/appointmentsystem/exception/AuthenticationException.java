package com.appointmentsystem.exception;

/**
 * Thrown when authentication fails.
 *
 * @author Mohammad
 * @version 1.0
 */
public class AuthenticationException extends RuntimeException {

    /**
     * Creates a new authentication exception.
     *
     * @param message error message
     */
    public AuthenticationException(String message) {
        super(message);
    }
}