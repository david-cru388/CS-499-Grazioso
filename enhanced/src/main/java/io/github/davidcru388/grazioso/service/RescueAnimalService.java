/*
 * File:     RescueAnimalService.java
 * Project:  CS-499-Grazioso
 * Author:   David Cruz
 * Date:     2026-11-13
 * Version:  1.0
 *
 * Purpose:
 *   Provides rescue animal operations and enforces reservation rules.
 *
 * Design notes:
 *   Receives its repository as a constructor dependency so storage
 *   implementations can be replaced without changing service behavior.
 *   availableAnimals() filters in O(n) and sorts the k available animals in
 *   O(k log k).
 * 
 */

package io.github.davidcru388.grazioso.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import io.github.davidcru388.grazioso.model.RescueAnimal;
import io.github.davidcru388.grazioso.model.TrainingStatus;
import io.github.davidcru388.grazioso.repository.AnimalRepository;
import io.github.davidcru388.grazioso.validation.ValidationException;

public class RescueAnimalService {
    private final AnimalRepository repository;

    public RescueAnimalService(AnimalRepository repository) {
        this.repository = repository;
    }

    public RescueAnimal intake(RescueAnimal animal) {
        return repository.save(animal);
    }

    public List<RescueAnimal> findByName(String name) {
        return repository.findByName(name);
    }

    public List<RescueAnimal> findAll() {
        return repository.findAll();
    }

    public RescueAnimal reserve(long id) {
        RescueAnimal animal = repository.findById(id)
                .orElseThrow(() -> new ValidationException("No animal found with ID " + id + "."));

        if (animal.getTrainingStatus() != TrainingStatus.IN_SERVICE) {
            throw new ValidationException("Only in-service animals can be reserved.");
        }
        if (animal.isReserved()) {
            throw new ValidationException("This animal is already reserved.");
        }

        animal.setReserved(true);
        return animal;
    }

    public List<RescueAnimal> availableAnimals() {
        List<RescueAnimal> available = new ArrayList<>();
        for (RescueAnimal animal : repository.findAll()) {
            if (animal.getTrainingStatus() == TrainingStatus.IN_SERVICE && !animal.isReserved()) {
                available.add(animal);
            }
        }

        // Sort only the k available animals with O(k log k) sorting.
        // The ID tie-breaker keeps same-name animals in order.

        available.sort(Comparator
                .comparing(RescueAnimal::getName, String.CASE_INSENSITIVE_ORDER)
                .thenComparingLong(RescueAnimal::getId));
        return available;
    }
}
