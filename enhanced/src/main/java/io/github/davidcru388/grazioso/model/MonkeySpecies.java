/*
 * File:     MonkeySpecies.java
 * Project:  CS-499-Grazioso
 * Author:   David Cruz
 * Date:     2026-11-09
 * Version:  1.0
 *
 * Purpose:
 *   Defines the monkey species in the rescue system.
 *
 * Design notes:
 *   Using a fixed set of species ensures valid entries and prevents nonsensical 
 *   inputs like 'cheeseburger.' This list includes the original sample species 
 *   (marmoset, guenon, squirrel monkey) along with common service species 
 *   (capuchin, macaque, tamarin).
 *   
 */

package io.github.davidcru388.grazioso.model;

public enum MonkeySpecies {
    MARMOSET("Marmoset"),
    GUENON("Guenon"),
    SQUIRREL_MONKEY("Squirrel monkey"),
    CAPUCHIN("Capuchin"),
    MACAQUE("Macaque"),
    TAMARIN("Tamarin");

    private final String displayName;

    MonkeySpecies(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
