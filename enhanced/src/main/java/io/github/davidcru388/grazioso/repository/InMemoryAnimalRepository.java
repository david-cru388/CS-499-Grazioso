/*
 * File:     InMemoryAnimalRepository.java
 * Project:  CS-499-Grazioso
 * Author:   David Cruz
 * Date:     2026-11-13
 * Version:  1.0
 *
 * Purpose:
 *   Stores rescue animals in memory for the current application run.
 *
 * Design notes:
 *   Uses linear scans deliberately to provide an O(n) baseline for the E2
 *   HashMap optimization comparison.
 */

package io.github.davidcru388.grazioso.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import io.github.davidcru388.grazioso.model.RescueAnimal;

public class InMemoryAnimalRepository implements AnimalRepository {
    private final List<RescueAnimal> animals = new ArrayList<>();
    private long nextId = 1;

    @Override
    public RescueAnimal save(RescueAnimal animal) {
        animal.assignId(nextId++);
        animals.add(animal);
        return animal;
    }

    @Override
    public Optional<RescueAnimal> findById(long id) {
        // Keep the linear scan as the O(n) baseline for the E2 comparison.
        for (RescueAnimal animal : animals) {
            if (animal.getId() == id) {
                return Optional.of(animal);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<RescueAnimal> findByName(String name) {
        // Keep the linear scan as the O(n) baseline for the E2 comparison.
        List<RescueAnimal> matches = new ArrayList<>();
        for (RescueAnimal animal : animals) {
            if (animal.getName().equalsIgnoreCase(name)) {
                matches.add(animal);
            }
        }
        return matches;
    }

    @Override
    public List<RescueAnimal> findAll() {
        return List.copyOf(animals);
    }
}
