/*
 * File:     HashMapAnimalRepository.java
 * Project:  CS-499-Grazioso
 * Author:   David Cruz
 * Date:     2026-11-18
 * Version:  1.0
 *
 * Purpose:
 *   Stores rescue animals using maps indexed by ID and name.
 *
 * Design notes:
 *   Replaces linear scans with hash indexes behind the same repository
 *   interface, so the service and menu do not change. 
 */

package io.github.davidcru388.grazioso.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import io.github.davidcru388.grazioso.model.RescueAnimal;

public class HashMapAnimalRepository implements AnimalRepository {
    // LinkedHashMap preserves intake order while retaining O(1) average lookups;
    // its insertion-order links use a little more memory than a plain HashMap.
    private final Map<Long, RescueAnimal> byId = new LinkedHashMap<>();
    private final Map<String, List<RescueAnimal>> byName = new HashMap<>();
    private long nextId = 1;

    private static String nameKey(String name) {
        // Locale.ROOT avoids language-specific casing (for example Turkish I).
        // Names are ASCII-validated, so Unicode normalization is unnecessary.
        return name.strip().toLowerCase(Locale.ROOT);
    }

    @Override
    public RescueAnimal save(RescueAnimal animal) {
        // Keep both indexes in sync; this is the only method that writes either map.
        animal.assignId(nextId++);
        byId.put(animal.getId(), animal);
        byName.computeIfAbsent(nameKey(animal.getName()), k -> new ArrayList<>()).add(animal);
        return animal;
    }

    @Override
    public Optional<RescueAnimal> findById(long id) {
        // O(1) average lookup, compared with the O(n) scan in InMemoryAnimalRepository.
        return Optional.ofNullable(byId.get(id));
    }

    @Override
    public List<RescueAnimal> findByName(String name) {
        if (name == null) {
            return List.of();
        }
        // O(1) average lookup plus k matches; the immutable copy protects the index.
        return List.copyOf(byName.getOrDefault(nameKey(name), List.of()));
    }

    @Override
    public List<RescueAnimal> findAll() {
        // O(n) is necessary because this method returns every animal.
        return List.copyOf(byId.values());
    }
}
