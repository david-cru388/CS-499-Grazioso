/*
 * File:     TrainingStatus.java
 * Project:  CS-499-Grazioso
 * Author:   David Cruz
 * Date:     2026-11-09
 * Version:  1.0
 *
 * Purpose:
 *   Defines the training and service stages used to track an animal's 
 *   training status and progress.
 * 
 * Design notes:
 *   The original sample data only demonstrated 'Phase I' and 'In service',
 *   without defining a complete set of training stages. The stages in this 
 *   enum add more progression for consistently tracking an animal’s status. 
 *
 */

package io.github.davidcru388.grazioso.model;

public enum TrainingStatus {
    INTAKE("Intake"),
    PHASE_I("Phase I"),
    PHASE_II("Phase II"),
    PHASE_III("Phase III"),
    PHASE_IV("Phase IV"),
    PHASE_V("Phase V"),
    IN_SERVICE("In service");

    private final String displayName;

    TrainingStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
