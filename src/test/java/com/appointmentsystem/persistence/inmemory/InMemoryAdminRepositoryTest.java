package com.appointmentsystem.persistence.inmemory;

import com.appointmentsystem.domain.Administrator;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryAdminRepositoryTest {

    // ===============================
    // ✅ Test: default admin exists
    // ===============================
    @Test
    void testDefaultAdminExists() {
        InMemoryAdminRepository repo = new InMemoryAdminRepository();

        Optional<Administrator> admin = repo.findByUsername("admin");

        assertTrue(admin.isPresent());
        assertEquals("admin", admin.get().getUsername());
    }

    // ===============================
    // ❌ Test: admin not found
    // ===============================
    @Test
    void testAdminNotFound() {
        InMemoryAdminRepository repo = new InMemoryAdminRepository();

        Optional<Administrator> admin = repo.findByUsername("unknown");

        assertFalse(admin.isPresent());
    }

    // ===============================
    // ⚠️ Test: null username
    // ===============================
    @Test
    void testFindByUsernameNull() {
        InMemoryAdminRepository repo = new InMemoryAdminRepository();

        Optional<Administrator> admin = repo.findByUsername(null);

        assertFalse(admin.isPresent());
    }

    // ===============================
    // ⚠️ Test: empty username
    // ===============================
    @Test
    void testFindByUsernameEmpty() {
        InMemoryAdminRepository repo = new InMemoryAdminRepository();

        Optional<Administrator> admin = repo.findByUsername("");

        assertFalse(admin.isPresent());
    }
}

