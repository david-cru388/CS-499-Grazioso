/*
 * File:     Gender.java
 * Project:  CS-499-Grazioso
 * Author:   David Cruz
 * Date:     2026-11-09
 * Version:  1.0
 *
 * Purpose:
 *   Defines the gender values for animals in the rescue system.
 *
 * Design notes:
 *   Replaces the original string gender field with a fixed set of 
 *   enum values, so the model can represent only the allowed options.
 */

package io.github.davidcru388.grazioso.model;

public enum Gender {
    MALE("Male"),
    FEMALE("Female");

    private final String displayName;

    Gender(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
