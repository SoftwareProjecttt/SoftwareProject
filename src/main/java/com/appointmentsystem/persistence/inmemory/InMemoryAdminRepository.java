package com.appointmentsystem.persistence.inmemory;

import com.appointmentsystem.domain.Administrator;
import com.appointmentsystem.persistence.AdminRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * In-memory implementation of administrator repository.
 *
 * @author Mohammad
 * @version 1.0
 */
public class InMemoryAdminRepository implements AdminRepository {

    /** In-memory list of administrators. */
    private final List<Administrator> administrators = new ArrayList<>();

    /**
     * Creates repository and seeds a default admin account.
     */
    public InMemoryAdminRepository() {
        administrators.add(new Administrator("admin", "1234"));
    }

    /**
     * Finds an administrator by username.
     *
     * @param username username to search for
     * @return optional administrator
     */
    @Override
    public Optional<Administrator> findByUsername(String username) {
        return administrators.stream()
                .filter(admin -> admin.getUsername().equals(username))
                .findFirst();
    }
}