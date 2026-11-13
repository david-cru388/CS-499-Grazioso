/*
 * File:     Monkey.java
 * Project:  CS-499-Grazioso
 * Author:   David Cruz
 * Date:     2026-11-13
 * Version:  1.0
 *
 * Purpose:
 *   Represents a monkey in the rescue animal system.
 *
 * Design Notes:
 *   Species validation is restricted to a predefined enum to prevent invalid text input.
 *   The defined measurement ranges are intended as validation bounds to 
 *   intercept data entry errors, rather than absolute maximums/minimums.
 */

package io.github.davidcru388.grazioso.model;

import java.time.LocalDate;

import io.github.davidcru388.grazioso.validation.InputValidator;
import io.github.davidcru388.grazioso.validation.ValidationException;

public final class Monkey extends RescueAnimal {
    public static final double MIN_WEIGHT_LB = 0.1;
    public static final double MAX_WEIGHT_LB = 60;

    public static final double MIN_TAIL_LENGTH_IN = 1;
    public static final double MAX_TAIL_LENGTH_IN = 40;

    public static final double MIN_HEIGHT_IN = 1;
    public static final double MAX_HEIGHT_IN = 48;

    public static final double MIN_BODY_LENGTH_IN = 1;
    public static final double MAX_BODY_LENGTH_IN = 40;

    // A fixed enum prevents random words from being accepted as species.
    private final MonkeySpecies species;
    private final double tailLengthIn;
    private final double heightIn;
    private final double bodyLengthIn;

    public Monkey(
            String name,
            Gender gender,
            int ageYears,
            double weightLb,
            LocalDate acquisitionDate,
            String acquisitionCountry,
            TrainingStatus trainingStatus,
            boolean reserved,
            String inServiceCountry,
            MonkeySpecies species,
            double tailLengthIn,
            double heightIn,
            double bodyLengthIn) {
        super(
                AnimalType.MONKEY,
                name,
                gender,
                ageYears,
                weightLb,
                MIN_WEIGHT_LB,
                MAX_WEIGHT_LB,
                acquisitionDate,
                acquisitionCountry,
                trainingStatus,
                reserved,
                inServiceCountry);
        if (species == null) {
            throw new ValidationException("Species is required.");
        }

        this.species = species;
        this.tailLengthIn = InputValidator.requireInRange(
                tailLengthIn, "Tail length", MIN_TAIL_LENGTH_IN, MAX_TAIL_LENGTH_IN);
        this.heightIn = InputValidator.requireInRange(
                heightIn, "Height", MIN_HEIGHT_IN, MAX_HEIGHT_IN);
        this.bodyLengthIn = InputValidator.requireInRange(
                bodyLengthIn, "Body length", MIN_BODY_LENGTH_IN, MAX_BODY_LENGTH_IN);
    }

    public MonkeySpecies getSpecies() {
        return species;
    }

    public double getTailLengthIn() {
        return tailLengthIn;
    }

    public double getHeightIn() {
        return heightIn;
    }

    public double getBodyLengthIn() {
        return bodyLengthIn;
    }
}
