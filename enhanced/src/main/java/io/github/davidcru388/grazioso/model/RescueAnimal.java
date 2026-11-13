/*
 * File:     RescueAnimal.java
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
import io.github.davidcru388.grazioso.validation.ValidationException;

public abstract class RescueAnimal {
    public static final int MIN_AGE = 0;
    public static final int MAX_AGE = 30;

    private long id;
    private final AnimalType type;
    private final String name;
    private final Gender gender;
    private final int ageYears;
    private final double weightLb;
    private final LocalDate acquisitionDate;
    private final String acquisitionCountry;
    private TrainingStatus trainingStatus;
    private boolean reserved;
    private String inServiceCountry;

    protected RescueAnimal(
            AnimalType type,
            String name,
            Gender gender,
            int ageYears,
            double weightLb,
            double minWeightLb,
            double maxWeightLb,
            LocalDate acquisitionDate,
            String acquisitionCountry,
            TrainingStatus trainingStatus,
            boolean reserved,
            String inServiceCountry) {
        if (type == null) {
            throw new ValidationException("Animal type is required.");
        }
        if (gender == null) {
            throw new ValidationException("Gender is required.");
        }
        if (trainingStatus == null) {
            throw new ValidationException("Training status is required.");
        }
        if (acquisitionDate == null) {
            throw new ValidationException("Acquisition date is required.");
        }
        if (acquisitionDate.isAfter(LocalDate.now())) {
            throw new ValidationException("Acquisition date cannot be in the future.");
        }

        this.type = type;
        this.name = InputValidator.validateName(name);
        this.gender = gender;
        this.ageYears = InputValidator.requireInRange(ageYears, "Age", MIN_AGE, MAX_AGE);
        this.weightLb = InputValidator.requireInRange(
                weightLb, "Weight", minWeightLb, maxWeightLb);
        this.acquisitionDate = acquisitionDate;
        this.acquisitionCountry = InputValidator.validateText(acquisitionCountry, "Acquisition country");
        this.trainingStatus = trainingStatus;
        this.reserved = reserved;
        this.inServiceCountry = InputValidator.validateText(inServiceCountry, "In-service country");
    }

    public long getId() {
        return id;
    }

    public void assignId(long id) {
        if (this.id != 0) {
            throw new IllegalStateException("Animal ID has already been assigned.");
        }
        if (id <= 0) {
            throw new IllegalArgumentException("Animal ID must be greater than zero.");
        }
        this.id = id;
    }

    public AnimalType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public Gender getGender() {
        return gender;
    }

    public int getAgeYears() {
        return ageYears;
    }

    public double getWeightLb() {
        return weightLb;
    }

    public LocalDate getAcquisitionDate() {
        return acquisitionDate;
    }

    public String getAcquisitionCountry() {
        return acquisitionCountry;
    }

    public TrainingStatus getTrainingStatus() {
        return trainingStatus;
    }

    public void setTrainingStatus(TrainingStatus trainingStatus) {
        if (trainingStatus == null) {
            throw new ValidationException("Training status is required.");
        }
        this.trainingStatus = trainingStatus;
    }

    public boolean isReserved() {
        return reserved;
    }

    public void setReserved(boolean reserved) {
        this.reserved = reserved;
    }

    public String getInServiceCountry() {
        return inServiceCountry;
    }

    public void setInServiceCountry(String inServiceCountry) {
        this.inServiceCountry = InputValidator.validateText(inServiceCountry, "In-service country");
    }
}