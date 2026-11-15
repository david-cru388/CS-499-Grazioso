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
 *   Rejects invalid input with a clear, simple error message instead of confusing technical details.
 * 
 */

package io.github.davidcru388.grazioso.validation;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}