package com.appointmentsystem.service;

import com.appointmentsystem.Main;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Unit test for Main.
 *
 * @author Mohammad
 * @version 1.0
 */
class MainTest {

    @Test
    void main_runsAndExitsCleanly() {
        System.setIn(new ByteArrayInputStream("3\n".getBytes()));
        assertDoesNotThrow(() -> Main.main(new String[0]));
    }
}
