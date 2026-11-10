/*
 * File:     InputValidator.java
 * Project:  CS-499-Grazioso
 * Author:   David Cruz
 * Date:     2026-11-10
 * Version:  1.0
 *
 * Purpose:
 *   Provides reusable validation and parsing for values entered into the
 *   rescue animal application.
 *
 * Design notes:
 *   Centralizes input rules and reports invalid values through
 *   ValidationException instead of returning defaults or leaking parse errors.
 */

package io.github.davidcru388.grazioso.validation;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

public final class InputValidator {
    //  accepts letters from any writing system, not just ASCII.
    private static final Pattern NAME = Pattern.compile("[\\p{L} '-]{1,40}");

    private InputValidator() {
    }

    public static int parseAge(String input) {
        return parseIntInRange(input, "Age", 0, 30);
    }

    public static int parseMenuChoice(String input, int max) {
        return parseIntInRange(input, "Menu choice", 1, max);
    }

    private static int parseIntInRange(String input, String fieldName, int min, int max) {
        if (input == null || input.isBlank()) {
            throw new ValidationException(fieldName + " is required.");
        }

        int value;
        try {
            value = Integer.parseInt(input.strip());
        } catch (NumberFormatException e) {
            // Replace Java's parsing detail with a concise message for the user.
            throw new ValidationException(fieldName + " must be a whole number.");
        }

        if (value < min || value > max) {
            throw new ValidationException(
                    fieldName + " must be between " + min + " and " + max + ".");
        }
        return value;
    }

    public static boolean parseYesNo(String input) {
        if (input == null || input.isBlank()) {
            throw new ValidationException("A yes or no answer is required.");
        }

        String answer = input.strip();
        // Accept true/false because the original program used those responses.
        if (answer.equalsIgnoreCase("yes") || answer.equalsIgnoreCase("y")
                || answer.equalsIgnoreCase("true")) {
            return true;
        }
        if (answer.equalsIgnoreCase("no") || answer.equalsIgnoreCase("n")
                || answer.equalsIgnoreCase("false")) {
            return false;
        }
        throw new ValidationException("Answer must be yes or no.");
    }

    public static double parseMeasurement(String input, String fieldName, double min, double max) {
        if (input == null || input.isBlank()) {
            throw new ValidationException(fieldName + " is required.");
        }

        double measurement;
        try {
            measurement = Double.parseDouble(input.strip());
        } catch (NumberFormatException e) {
            // Keep implementation-specific parsing details out of user-facing errors.
            throw new ValidationException(fieldName + " must be a number.");
        }

        if (!Double.isFinite(measurement) || measurement < min || measurement > max) {
            throw new ValidationException(
                    fieldName + " must be a finite number between " + min + " and " + max + ".");
        }
        return measurement;
    }

    public static LocalDate parseDate(String input) {
        if (input == null || input.isBlank()) {
            throw new ValidationException("Date is required.");
        }

        LocalDate date;
        try {
            date = LocalDate.parse(input.strip());
        } catch (DateTimeParseException e) {
            // Give callers a stable, readable message instead of the parser's details.
            throw new ValidationException("Date must use yyyy-MM-dd format.");
        }

        if (date.isAfter(LocalDate.now())) {
            throw new ValidationException("Date cannot be in the future.");
        }
        return date;
    }

    public static String validateName(String input) {
        if (input == null || input.isBlank()) {
            throw new ValidationException("Name is required.");
        }

        String name = input.strip();
        // matches() checks the entire name, so a valid prefix cannot hide extra input.
        if (!NAME.matcher(name).matches()
                || name.codePoints().noneMatch(Character::isLetter)) {
            throw new ValidationException(
                    "Name must contain 1 to 40 letters, spaces, apostrophes, or hyphens.");
        }
        return name;
    }
}