/*
 * File:     ValidationException.java
 * Project:  CS-499-Grazioso
 * Author:   David Cruz
 * Date:     2026-11-10
 * Version:  1.0
 *
 * Purpose:
 *   Represents invalid user input with a clear, user-facing message.
 *
 * Design notes:
 *   Uses an unchecked exception so validator methods can reject invalid input
 *   without exposing low-level parsing exceptions to callers.
 */

package io.github.davidcru388.grazioso.validation;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}