package com.appointmentsystem.observer;

/**
 * Represents the type of appointment lifecycle event fired by a subject.
 *
 * <p>Using an enum instead of raw Strings ensures that only valid event
 * types can be passed to observers, eliminating typo-related bugs and
 * enabling exhaustive switch statements.</p>
 *
 * @author Mohammad
 * @version 1.0
 */
public enum AppointmentEventType {

    /** Fired when an appointment is successfully created and saved. */
    BOOKED,

    /** Fired when a confirmed future appointment is cancelled. */
    CANCELLED,

    /** Fired when a confirmed future appointment is moved to a different slot. */
    MODIFIED
}
