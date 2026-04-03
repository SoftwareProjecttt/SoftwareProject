package com.appointmentsystem.security;

import com.appointmentsystem.domain.Administrator;

/**
 * Manages the current administrator session.
 *
 * @author Mohammad
 * @version 1.0
 */
public class SessionManager {

    /** Currently logged-in administrator. */
    private Administrator loggedInAdmin;

    /**
     * Starts a session for the given administrator.
     *
     * @param administrator authenticated administrator
     */
    public void login(Administrator administrator) {
        this.loggedInAdmin = administrator;
    }

    /**
     * Ends the current session.
     */
    public void logout() {
        this.loggedInAdmin = null;
    }

    /**
     * Checks if there is an authenticated administrator.
     *
     * @return true if authenticated, otherwise false
     */
    public boolean isAuthenticated() {
        return loggedInAdmin != null;
    }

    /**
     * Returns the logged-in administrator.
     *
     * @return logged-in administrator or null
     */
    public Administrator getLoggedInAdmin() {
        return loggedInAdmin;
    }
}