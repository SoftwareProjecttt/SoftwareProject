package com.appointmentsystem.domain;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Represents an available appointment time slot.
 *
 * @author Mohammad
 * @version 3.0
 */
public class TimeSlot {

    /** Slot identifier. */
    private final String id;

    /** Slot date. */
    private final LocalDate date;

    /** Start time. */
    private final LocalTime startTime;

    /** End time. */
    private final LocalTime endTime;

    /** Maximum number of participants allowed in this slot. */
    private final int maxCapacity;

    /** Current number of booked participants. */
    private int bookedCount;

    /**
     * Creates a new time slot.
     *
     * @param id slot id
     * @param date slot date
     * @param startTime start time
     * @param endTime end time
     * @param maxCapacity maximum capacity
     * @param bookedCount current booked count
     */
    public TimeSlot(String id, LocalDate date, LocalTime startTime, LocalTime endTime,
                    int maxCapacity, int bookedCount) {
        this.id = id;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.maxCapacity = maxCapacity;
        this.bookedCount = bookedCount;
    }

    /**
     * Returns slot id.
     *
     * @return slot id
     */
    public String getId() {
        return id;
    }

    /**
     * Returns slot date.
     *
     * @return slot date
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Returns start time.
     *
     * @return start time
     */
    public LocalTime getStartTime() {
        return startTime;
    }

    /**
     * Returns end time.
     *
     * @return end time
     */
    public LocalTime getEndTime() {
        return endTime;
    }

    /**
     * Returns maximum capacity.
     *
     * @return maximum capacity
     */
    public int getMaxCapacity() {
        return maxCapacity;
    }

    /**
     * Returns current booked count.
     *
     * @return booked count
     */
    public int getBookedCount() {
        return bookedCount;
    }

    /**
     * Returns slot duration in minutes.
     *
     * @return duration in minutes
     */
    public long getDurationInMinutes() {
        return Duration.between(startTime, endTime).toMinutes();
    }

    /**
     * Returns remaining capacity.
     *
     * @return remaining capacity
     */
    public int getRemainingCapacity() {
        return maxCapacity - bookedCount;
    }

    /**
     * Checks whether the slot is still available.
     *
     * @return true if available, otherwise false
     */
    public boolean isAvailable() {
        return bookedCount < maxCapacity;
    }

    /**
     * Books participants into this slot.
     *
     * @param participantCount number of participants to add
     */
    public void bookParticipants(int participantCount) {
        this.bookedCount += participantCount;
    }

    /**
     * Releases participants from this slot.
     *
     * @param participantCount number of participants to remove
     */
    public void releaseParticipants(int participantCount) {
        this.bookedCount -= participantCount;

        if (this.bookedCount < 0) {
            this.bookedCount = 0;
        }
    }

    /**
     * Returns a text representation of the slot.
     *
     * @return formatted slot information
     */
    @Override
    public String toString() {
        return "Slot ID: " + id
                + " | Date: " + date
                + " | Time: " + startTime + " - " + endTime
                + " | Booked: " + bookedCount + "/" + maxCapacity;
    }
}