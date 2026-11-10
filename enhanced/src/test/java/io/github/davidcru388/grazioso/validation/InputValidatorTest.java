/*
 * File:     InputValidatorTest.java
 * Project:  CS-499-Grazioso
 * Author:   David Cruz
 * Date:     2026-11-10
 * Version:  1.0
 *
 * Purpose:
 *   Tests valid and invalid input handling by the application's validator.
 *
 * Design notes:
 *   Covers validation boundaries and representative malformed inputs without
 *   depending on a fixed current date.
 */

package io.github.davidcru388.grazioso.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class InputValidatorTest {

    @Test
    void acceptsAgeBoundaries() {
        assertEquals(0, InputValidator.parseAge("0"));
        assertEquals(30, InputValidator.parseAge("30"));
    }

    @Test
    void rejectsAgeOutsideRange() {
        assertThrows(ValidationException.class, () -> InputValidator.parseAge("-1"));
        assertThrows(ValidationException.class, () -> InputValidator.parseAge("31"));
    }

    @Test
    void rejectsInvalidAgeText() {
        ValidationException exception = assertThrows(
                ValidationException.class, () -> InputValidator.parseAge("abc"));
        assertEquals("Age must be a whole number.", exception.getMessage());
        assertThrows(ValidationException.class, () -> InputValidator.parseAge(" "));
        assertThrows(ValidationException.class, () -> InputValidator.parseAge(null));
    }

    @Test
    void acceptsMenuChoiceBoundaries() {
        assertEquals(1, InputValidator.parseMenuChoice("1", 6));
        assertEquals(6, InputValidator.parseMenuChoice("6", 6));
        assertEquals(7, InputValidator.parseMenuChoice("7", 7));
    }

    @Test
    void rejectsInvalidMenuChoices() {
        assertThrows(ValidationException.class, () -> InputValidator.parseMenuChoice("0", 6));
        assertThrows(ValidationException.class, () -> InputValidator.parseMenuChoice("3", 2));
        assertThrows(ValidationException.class, () -> InputValidator.parseMenuChoice("q", 6));
    }

    @Test
    void parsesYesNoAndBooleanWords() {
        assertTrue(InputValidator.parseYesNo("yes"));
        assertTrue(InputValidator.parseYesNo("y"));
        assertTrue(InputValidator.parseYesNo("TRUE"));
        assertFalse(InputValidator.parseYesNo("no"));
        assertFalse(InputValidator.parseYesNo("n"));
        assertFalse(InputValidator.parseYesNo("False"));
    }

    @Test
    void rejectsInvalidYesNoInput() {
        assertThrows(ValidationException.class, () -> InputValidator.parseYesNo("maybe"));
        assertThrows(ValidationException.class, () -> InputValidator.parseYesNo(null));
    }

    @Test
    void acceptsPositiveMeasurements() {
        assertEquals(25.6, InputValidator.parseMeasurement("25.6", "Weight", 0.1, 100.0), 0.0001);
        assertEquals(1.0, InputValidator.parseMeasurement(" 1 ", "Height", 0.1, 100.0), 0.0001);
    }

    @Test
    void rejectsInvalidMeasurements() {
        assertThrows(ValidationException.class,
                () -> InputValidator.parseMeasurement("abc", "Weight", 0.1, 100.0));
        assertThrows(ValidationException.class,
                () -> InputValidator.parseMeasurement("0", "Weight", 0.1, 100.0));
        assertThrows(ValidationException.class,
                () -> InputValidator.parseMeasurement("-1", "Weight", 0.1, 100.0));
        assertThrows(ValidationException.class,
                () -> InputValidator.parseMeasurement("100.1", "Weight", 0.1, 100.0));
    }

    @Test
    void parsesTodayAndPastDates() {
        LocalDate today = LocalDate.now();
        assertEquals(today, InputValidator.parseDate(today.toString()));
        assertEquals(LocalDate.of(2020, 2, 3), InputValidator.parseDate("2020-02-03"));
    }

    @Test
    void rejectsMalformedAndFutureDates() {
        assertThrows(ValidationException.class, () -> InputValidator.parseDate("02-03-2020"));
        assertThrows(ValidationException.class,
                () -> InputValidator.parseDate(LocalDate.now().plusDays(1).toString()));
    }

    @Test
    void acceptsNamesWithUnicodeLettersAndStripsWhitespace() {
        assertEquals("Anne-Marie O'Neil", InputValidator.validateName(" Anne-Marie O'Neil "));
        assertEquals("José", InputValidator.validateName("José"));
        assertEquals("Björn", InputValidator.validateName("Björn"));
        assertEquals("A".repeat(40), InputValidator.validateName("A".repeat(40)));
    }

    @Test
    void rejectsInvalidNames() {
        assertThrows(ValidationException.class, () -> InputValidator.validateName(""));
        assertThrows(ValidationException.class, () -> InputValidator.validateName("Name123"));
        assertThrows(ValidationException.class, () -> InputValidator.validateName("-"));
        assertThrows(ValidationException.class, () -> InputValidator.validateName("' '"));
        assertThrows(ValidationException.class, () -> InputValidator.validateName("Rex\u0007"));
        assertThrows(ValidationException.class,
                () -> InputValidator.validateName("A".repeat(41)));
        assertThrows(ValidationException.class, () -> InputValidator.validateName(null));
    }
}