/*
 * File:     AnimalRepository.java
 * Project:  CS-499-Grazioso
 * Author:   David Cruz
 * Date:     2026-11-13
 * Version:  1.0
 *
 * Purpose:
 *   Defines the storage operations used by the rescue animal service.
 *
 * Design notes:
 *   The service depends on this interface rather than a specific storage
 *   implementation, allowing E2 (HashMap) and E3 (SQLite) repositories to
 *   replace the current implementation without changing service code.
 */

package io.github.davidcru388.grazioso.repository;

import java.util.List;
import java.util.Optional;

import io.github.davidcru388.grazioso.model.RescueAnimal;

public interface AnimalRepository {
    RescueAnimal save(RescueAnimal animal);

    Optional<RescueAnimal> findById(long id);

    List<RescueAnimal> findByName(String name);

    List<RescueAnimal> findAll();
}
