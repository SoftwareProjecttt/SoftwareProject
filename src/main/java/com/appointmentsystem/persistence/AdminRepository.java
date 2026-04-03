package com.appointmentsystem.persistence;

import com.appointmentsystem.domain.Administrator;

import java.util.Optional;

/**
 * Repository for administrator data access.
 *
 * @author Mohammad
 * @version 1.0
 */
public interface AdminRepository {

    /**
     * Finds an administrator by username.
     *
     * @param username username to search for
     * @return optional administrator
     */
    Optional<Administrator> findByUsername(String username);
}