package com.appointmentsystem.persistence.inmemory;

import com.appointmentsystem.domain.TimeSlot;
import com.appointmentsystem.persistence.TimeSlotRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * In-memory implementation of time slot repository.
 *
 * @author Mohammad
 * @version 2.0
 */
public class InMemoryTimeSlotRepository implements TimeSlotRepository {

    /** In-memory list of time slots. */
    private final List<TimeSlot> timeSlots = new ArrayList<>();

    /**
     * Creates repository and seeds sample appointment slots.
     */
    public InMemoryTimeSlotRepository() {
        timeSlots.add(new TimeSlot("TS1", LocalDate.now().plusDays(1),
                LocalTime.of(9, 0), LocalTime.of(10, 0), 3, 1));

        timeSlots.add(new TimeSlot("TS2", LocalDate.now().plusDays(1),
                LocalTime.of(10, 0), LocalTime.of(11, 0), 2, 2));

        timeSlots.add(new TimeSlot("TS3", LocalDate.now().plusDays(2),
                LocalTime.of(11, 0), LocalTime.of(12, 0), 4, 1));

        timeSlots.add(new TimeSlot("TS4", LocalDate.now().plusDays(2),
                LocalTime.of(13, 0), LocalTime.of(14, 0), 1, 0));
    }

    /**
     * Returns all slots.
     *
     * @return list of time slots
     */
    @Override
    public List<TimeSlot> findAll() {
        return new ArrayList<>(timeSlots);
    }

    /**
     * Finds a slot by id.
     *
     * @param id slot id
     * @return optional time slot
     */
    @Override
    public Optional<TimeSlot> findById(String id) {
        return timeSlots.stream()
                .filter(slot -> slot.getId().equalsIgnoreCase(id))
                .findFirst();
    }
}