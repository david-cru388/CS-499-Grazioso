/*
 * File:     RescueAnimalServiceTest.java
 * Project:  CS-499-Grazioso
 * Author:   David Cruz
 * Date:     2026-11-13
 * Version:  1.0
 *
 * Purpose:
 *   Tests animal intake, name matching, and reservation behavior in the service.
 *
 * Design notes:
 *   Uses the in-memory repository to verify service behavior through its
 *   repository abstraction.
 */

package io.github.davidcru388.grazioso.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import io.github.davidcru388.grazioso.model.Dog;
import io.github.davidcru388.grazioso.model.Gender;
import io.github.davidcru388.grazioso.model.RescueAnimal;
import io.github.davidcru388.grazioso.model.TrainingStatus;
import io.github.davidcru388.grazioso.repository.InMemoryAnimalRepository;
import io.github.davidcru388.grazioso.validation.ValidationException;
import org.junit.jupiter.api.Test;

class RescueAnimalServiceTest {

    private RescueAnimalService createService() {
        return new RescueAnimalService(new InMemoryAnimalRepository());
    }

    private Dog dog(String name, TrainingStatus status) {
        return new Dog(
                name,
                Gender.MALE,
                3,
                35.2,
                LocalDate.of(2020, 2, 3),
                "United States",
                status,
                false,
                "United States",
                "Great Dane");
    }

    @Test
    void assignsUniquePositiveIdsOnIntake() {
        RescueAnimalService service = createService();
        RescueAnimal first = service.intake(dog("Rex", TrainingStatus.PHASE_I));
        RescueAnimal second = service.intake(dog("Bella", TrainingStatus.PHASE_I));

        assertTrue(first.getId() > 0);
        assertTrue(second.getId() > 0);
        assertNotEquals(first.getId(), second.getId());
    }

    @Test
    void allowsMultipleAnimalsWithTheSameName() {
        RescueAnimalService service = createService();
        service.intake(dog("Spot", TrainingStatus.PHASE_I));
        service.intake(dog("Spot", TrainingStatus.PHASE_I));

        assertEquals(2, service.findByName("spot").size());
    }

    @Test
    void onlyReservesInServiceAnimals() {
        RescueAnimalService service = createService();
        RescueAnimal inTraining = service.intake(dog("Rex", TrainingStatus.PHASE_I));

        assertThrows(ValidationException.class, () -> service.reserve(inTraining.getId()));

        RescueAnimal inService = service.intake(dog("Bella", TrainingStatus.IN_SERVICE));
        RescueAnimal reserved = service.reserve(inService.getId());

        assertTrue(reserved.isReserved());
    }
}
