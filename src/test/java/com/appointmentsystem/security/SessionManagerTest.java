package com.appointmentsystem.security;

import com.appointmentsystem.domain.Administrator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SessionManagerTest {

    @Test
    void shouldLoginAndLogout() {
        SessionManager sessionManager = new SessionManager();
        Administrator admin = new Administrator("admin", "1234");

        sessionManager.login(admin);
        assertTrue(sessionManager.isAuthenticated());
        assertEquals(admin, sessionManager.getLoggedInAdmin());

        sessionManager.logout();
        assertFalse(sessionManager.isAuthenticated());
        assertNull(sessionManager.getLoggedInAdmin());
    }
}