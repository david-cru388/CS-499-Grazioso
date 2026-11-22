/*
 * File:     HashMapAnimalRepositoryTest.java
 * Project:  CS-499-Grazioso
 * Author:   David Cruz
 * Date:     2026-11-19
 * Version:  1.0
 *
 * Purpose:
 *   Tests behavior specific to the HashMap animal repository.
 *
 * Design notes:
 *   These tests cover HashMap-specific normalization, ordering, immutability,
 *   and duplicate-save behavior; shared repository behavior belongs in the
 *   parameterized service test suite.
 */

package io.github.davidcru388.grazioso.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.List;

import io.github.davidcru388.grazioso.model.Dog;
import io.github.davidcru388.grazioso.model.Gender;
import io.github.davidcru388.grazioso.model.RescueAnimal;
import io.github.davidcru388.grazioso.model.TrainingStatus;
import org.junit.jupiter.api.Test;

class HashMapAnimalRepositoryTest {

    private Dog dog(String name) {
        return new Dog(
                name,
                Gender.MALE,
                3,
                35.2,
                LocalDate.of(2020, 2, 3),
                "United States",
                TrainingStatus.PHASE_I,
                false,
                "United States",
                "Great Dane");
    }

    @Test
    void findsNameIgnoringCaseAndSurroundingWhitespace() {
        HashMapAnimalRepository repository = new HashMapAnimalRepository();
        RescueAnimal rex = repository.save(dog("Rex"));

        assertSame(rex, repository.findByName("Rex ").get(0));
        assertSame(rex, repository.findByName("REX").get(0));
        assertSame(rex, repository.findByName("rex").get(0));
    }

    @Test
    void findAllReturnsAnimalsInIntakeOrder() {
        HashMapAnimalRepository repository = new HashMapAnimalRepository();
        RescueAnimal first = repository.save(dog("Rex"));
        RescueAnimal second = repository.save(dog("Spot"));

        assertEquals(List.of(first, second), repository.findAll());
    }

    @Test
    void findByNameReturnsAnUnmodifiableList() {
        HashMapAnimalRepository repository = new HashMapAnimalRepository();
        repository.save(dog("Rex"));

        assertThrows(UnsupportedOperationException.class,
                () -> repository.findByName("Rex").add(dog("Spot")));
    }

    @Test
    void savingSameAnimalTwiceDoesNotAddDuplicateNameMatch() {
        HashMapAnimalRepository repository = new HashMapAnimalRepository();
        Dog rex = dog("Rex");
        repository.save(rex);

        assertThrows(IllegalStateException.class, () -> repository.save(rex));
        assertEquals(1, repository.findByName("Rex").size());
    }
}
