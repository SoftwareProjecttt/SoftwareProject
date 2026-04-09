package com.appointmentsystem;

import com.appointmentsystem.domain.Administrator;
import com.appointmentsystem.exception.AuthenticationException;
import com.appointmentsystem.persistence.AdminRepository;
import com.appointmentsystem.security.SessionManager;

/**
 * Handles administrator authentication and session actions.
 *
 * @author Mohammad
 * @version 1.0
 */
public class AuthService {

    /** Repository used to retrieve administrators. */
    private final AdminRepository adminRepository;

    /** Session manager used to track login state. */
    private final SessionManager sessionManager;

    /**
     * Creates a new authentication service.
     *
     * @param adminRepository admin repository
     * @param sessionManager session manager
     */
    public AuthService(AdminRepository adminRepository, SessionManager sessionManager) {
        this.adminRepository = adminRepository;
        this.sessionManager = sessionManager;
    }

    /**
     * Logs in an administrator using credentials.
     *
     * @param username entered username
     * @param password entered password
     * @throws AuthenticationException if credentials are invalid
     */
    public void login(String username, String password) {
        Administrator administrator = adminRepository.findByUsername(username)
                .orElseThrow(() -> new AuthenticationException("Admin not found."));

        if (!administrator.getPassword().equals(password)) {
            throw new AuthenticationException("Invalid password.");
        }

        sessionManager.login(administrator);
    }

    /**
     * Logs out the current administrator.
     */
    public void logout() {
        sessionManager.logout();
    }

    /**
     * Checks whether an administrator is currently logged in.
     *
     * @return true if authenticated, otherwise false
     */
    public boolean isAuthenticated() {
        return sessionManager.isAuthenticated();
    }

    /**
     * Returns the logged-in username.
     *
     * @return username or null if no session exists
     */
    public String getLoggedInUsername() {
        if (!sessionManager.isAuthenticated()) {
            return null;
        }
        return sessionManager.getLoggedInAdmin().getUsername();
    }
}