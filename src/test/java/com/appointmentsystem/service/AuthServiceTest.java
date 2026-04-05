 package com.appointmentsystem.service;

import com.appointmentsystem.domain.Administrator;
import com.appointmentsystem.exception.AuthenticationException;
import com.appointmentsystem.persistence.AdminRepository;
import com.appointmentsystem.security.SessionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private AdminRepository adminRepository;
    private SessionManager sessionManager;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        adminRepository = mock(AdminRepository.class);
        sessionManager = mock(SessionManager.class);
        authService = new AuthService(adminRepository, sessionManager);
    }

    @Test
    void login_success() {
        Administrator admin = new Administrator("admin", "1234");

        when(adminRepository.findByUsername("admin"))
                .thenReturn(Optional.of(admin));

        authService.login("admin", "1234");

        verify(sessionManager).login(admin);
    }

    @Test
    void login_adminNotFound() {
        when(adminRepository.findByUsername("admin"))
                .thenReturn(Optional.empty());

        assertThrows(AuthenticationException.class,
                () -> authService.login("admin", "1234"));
    }

    @Test
    void login_wrongPassword() {
        Administrator admin = new Administrator("admin", "1234");

        when(adminRepository.findByUsername("admin"))
                .thenReturn(Optional.of(admin));

        assertThrows(AuthenticationException.class,
                () -> authService.login("admin", "wrong"));
    }

    @Test
    void logout_callsSessionManager() {
        authService.logout();

        verify(sessionManager).logout();
    }

    @Test
    void isAuthenticated_true() {
        when(sessionManager.isAuthenticated()).thenReturn(true);

        assertTrue(authService.isAuthenticated());
    }

    @Test
    void isAuthenticated_false() {
        when(sessionManager.isAuthenticated()).thenReturn(false);

        assertFalse(authService.isAuthenticated());
    }

    @Test
    void getLoggedInUsername_whenAuthenticated() {
        Administrator admin = new Administrator("admin", "1234");

        when(sessionManager.isAuthenticated()).thenReturn(true);
        when(sessionManager.getLoggedInAdmin()).thenReturn(admin);

        String username = authService.getLoggedInUsername();

        assertEquals("admin", username);
    }

    @Test
    void getLoggedInUsername_whenNotAuthenticated() {
        when(sessionManager.isAuthenticated()).thenReturn(false);

        String username = authService.getLoggedInUsername();

        assertNull(username);
    }
}

