/*
 * File:     Dog.java
 * Project:  CS-499-Grazioso
 * Author:   David Cruz
 * Date:     2026-11-13
 * Version:  1.0
 *
 * Purpose:
 *   
 *
 * Design notes:
 *   
 */

package io.github.davidcru388.grazioso.model;

import java.time.LocalDate;

import io.github.davidcru388.grazioso.validation.InputValidator;

public final class Dog extends RescueAnimal {
    public static final double MIN_WEIGHT_LB = 1;
    public static final double MAX_WEIGHT_LB = 250;

    private final String breed;

    public Dog(
            String name,
            Gender gender,
            int ageYears,
            double weightLb,
            LocalDate acquisitionDate,
            String acquisitionCountry,
            TrainingStatus trainingStatus,
            boolean reserved,
            String inServiceCountry,
            String breed) {
        super(
                AnimalType.DOG,
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
        this.breed = InputValidator.validateText(breed, "Breed");
    }

    public String getBreed() {
        return breed;
    }
}