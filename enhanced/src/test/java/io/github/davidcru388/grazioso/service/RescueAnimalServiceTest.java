/*
 * File:     RescueAnimalServiceTest.java
 * Project:  CS-499-Grazioso
 * Author:   David Cruz
 * Date:     2026-11-17
 * Version:  1.0
 *
 * Purpose:
 *   Tests animal intake, lookup, and reservation behavior through the service.
 *
 * Design notes:
 *   Runs the same tests on both repositories to verify the HashMap
 *   implementation behaves like the O(n) in-memory baseline.
 */

package io.github.davidcru388.grazioso.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import io.github.davidcru388.grazioso.model.Dog;
import io.github.davidcru388.grazioso.model.Gender;
import io.github.davidcru388.grazioso.model.RescueAnimal;
import io.github.davidcru388.grazioso.model.TrainingStatus;
import io.github.davidcru388.grazioso.repository.AnimalRepository;
import io.github.davidcru388.grazioso.repository.HashMapAnimalRepository;
import io.github.davidcru388.grazioso.repository.InMemoryAnimalRepository;
import io.github.davidcru388.grazioso.repository.SqliteAnimalRepository;
import io.github.davidcru388.grazioso.validation.ValidationException;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class RescueAnimalServiceTest {

    static Stream<Named<Supplier<AnimalRepository>>> repositories() {
        return Stream.of(
                Named.of("InMemoryAnimalRepository", InMemoryAnimalRepository::new),
                Named.of("HashMapAnimalRepository", HashMapAnimalRepository::new),
                Named.of("SqliteAnimalRepository", RescueAnimalServiceTest::newSqliteRepository));
    }

    private static AnimalRepository newSqliteRepository() {
        try {
            Path file = Files.createTempFile("grazioso", ".db");
            file.toFile().deleteOnExit();
            return new SqliteAnimalRepository("jdbc:sqlite:" + file);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not create a temporary SQLite database.", e);
        }
    }

    private RescueAnimalService createService(Supplier<AnimalRepository> newRepository) {
        return new RescueAnimalService(newRepository.get());
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

    @ParameterizedTest(name = "{0}")
    @MethodSource("repositories")
    void assignsUniquePositiveIdsOnIntake(Supplier<AnimalRepository> newRepository) {
        RescueAnimalService service = createService(newRepository);
        RescueAnimal first = service.intake(dog("Rex", TrainingStatus.PHASE_I));
        RescueAnimal second = service.intake(dog("Bella", TrainingStatus.PHASE_I));

        assertTrue(first.getId() > 0);
        assertTrue(second.getId() > 0);
        assertNotEquals(first.getId(), second.getId());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("repositories")
    void allowsMultipleAnimalsWithTheSameName(Supplier<AnimalRepository> newRepository) {
        RescueAnimalService service = createService(newRepository);
        service.intake(dog("Spot", TrainingStatus.PHASE_I));
        service.intake(dog("Spot", TrainingStatus.PHASE_I));

        assertEquals(2, service.findByName("spot").size());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("repositories")
    void onlyReservesInServiceAnimals(Supplier<AnimalRepository> newRepository) {
        RescueAnimalService service = createService(newRepository);
        RescueAnimal inTraining = service.intake(dog("Rex", TrainingStatus.PHASE_I));

        assertThrows(ValidationException.class, () -> service.reserve(inTraining.getId()));

        RescueAnimal inService = service.intake(dog("Bella", TrainingStatus.IN_SERVICE));
        RescueAnimal reserved = service.reserve(inService.getId());

        assertTrue(reserved.isReserved());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("repositories")
    void rejectsReservingUnknownId(Supplier<AnimalRepository> newRepository) {
        RescueAnimalService service = createService(newRepository);

        assertThrows(ValidationException.class, () -> service.reserve(999));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("repositories")
    void rejectsReservingAnimalTwice(Supplier<AnimalRepository> newRepository) {
        RescueAnimalService service = createService(newRepository);
        RescueAnimal animal = service.intake(dog("Spot", TrainingStatus.IN_SERVICE));

        service.reserve(animal.getId());

        assertThrows(ValidationException.class, () -> service.reserve(animal.getId()));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("repositories")
    void listsOnlyAvailableInServiceAnimals(Supplier<AnimalRepository> newRepository) {
        RescueAnimalService service = createService(newRepository);
        service.intake(dog("Training", TrainingStatus.PHASE_I));
        Dog reserved = dog("Reserved", TrainingStatus.IN_SERVICE);
        reserved.setReserved(true);
        service.intake(reserved);
        RescueAnimal available = service.intake(dog("Available", TrainingStatus.IN_SERVICE));

        assertEquals(
                List.of(available.getId()),
                service.availableAnimals().stream().map(RescueAnimal::getId).toList());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("repositories")
    void availableAnimalsAreSortedByNameThenId(Supplier<AnimalRepository> newRepository) {
        RescueAnimalService service = createService(newRepository);
        RescueAnimal firstSpot = service.intake(dog("spot", TrainingStatus.IN_SERVICE));
        RescueAnimal bella = service.intake(dog("Bella", TrainingStatus.IN_SERVICE));
        RescueAnimal secondSpot = service.intake(dog("Spot", TrainingStatus.IN_SERVICE));

        assertEquals(
                List.of(bella.getId(), firstSpot.getId(), secondSpot.getId()),
                service.availableAnimals().stream().map(RescueAnimal::getId).toList());
    }
}
