package com.appointmentsystem.domain;

/**
 * Represents an administrator who can access scheduling management features.
 *
 * @author Mohammad
 * @version 1.0
 */
public class Administrator {
    /** Administrator username. */
    private final String username;

    /** Administrator password. */
    private final String password;

    /**
     * Creates a new administrator.
     *
     * @param username administrator username
     * @param password administrator password
     */
    public Administrator(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * Returns the administrator username.
     *
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the administrator password.
     *
     * @return password
     */
    public String getPassword() {
        return password;
    }
}