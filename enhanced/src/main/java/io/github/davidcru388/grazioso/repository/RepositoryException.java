/*
 * File:     RepositoryException.java
 * Project:  CS-499-Grazioso
 * Author:   David Cruz
 * Date:     2026-11-24
 * Version:  1.0
 *
 * Purpose:
 *   Reports storage failures without exposing JDBC details to service callers.
 *
 * Design notes:
 *   Wraps SQLException so the service and UI do not depend on JDBC, and user
 *   facing errors do not reveal SQL details.
 */

package io.github.davidcru388.grazioso.repository;

public class RepositoryException extends RuntimeException {
    public RepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}