package com.appointmentsystem.service;

import com.appointmentsystem.domain.TimeSlot;
import com.appointmentsystem.persistence.TimeSlotRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles scheduling-related operations.
 *
 * @author Mohammad
 * @version 1.0
 */
public class ScheduleService {

    /** Repository used to access time slots. */
    private final TimeSlotRepository timeSlotRepository;

    /**
     * Creates a new schedule service.
     *
     * @param timeSlotRepository time slot repository
     */
    public ScheduleService(TimeSlotRepository timeSlotRepository) {
        this.timeSlotRepository = timeSlotRepository;
    }

    /**
     * Returns only available appointment slots.
     *
     * @return list of available slots
     */
    public List<TimeSlot> getAvailableSlots() {
        return timeSlotRepository.findAll()
                .stream()
                .filter(TimeSlot::isAvailable)
                .collect(Collectors.toList());
    }
}