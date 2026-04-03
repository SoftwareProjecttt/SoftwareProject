package com.appointmentsystem.persistence;

import com.appointmentsystem.domain.TimeSlot;

import java.util.List;
import java.util.Optional;

/**
 * Repository for time slot persistence.
 *
 * @author Mohammad
 * @version 2.0
 */
public interface TimeSlotRepository {

    /**
     * Returns all time slots.
     *
     * @return list of time slots
     */
    List<TimeSlot> findAll();

    /**
     * Finds a slot by id.
     *
     * @param id slot id
     * @return optional time slot
     */
    Optional<TimeSlot> findById(String id);
}