/*
 * File:     AnimalType.java
 * Project:  CS-499-Grazioso
 * Author:   David Cruz
 * Date:     2026-11-09
 * Version:  1.0
 *
 * Purpose:
 *   Defines the types of animals in the rescue system.
 *  
 * Design notes:
 *   Defines the supported animal types as enum values. This replaces the 
 *   original animalType field, which was declared but not used, and provides a 
 *   consistent type for identifying animals.
 */

package io.github.davidcru388.grazioso.model;

public enum AnimalType {
    DOG("Dog"),
    MONKEY("Monkey");

    private final String displayName;

    AnimalType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
