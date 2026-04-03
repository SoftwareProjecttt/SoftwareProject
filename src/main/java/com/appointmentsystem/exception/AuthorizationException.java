package com.appointmentsystem.exception;

/**
 * Thrown when a user tries to perform an action without proper authorization.
 *
 * @author Mohammad
 * @version 1.0
 */
public class AuthorizationException extends RuntimeException {

    /**
     * Creates a new authorization exception.
     *
     * @param message exception message
     */
    public AuthorizationException(String message) {
        super(message);
    }
}