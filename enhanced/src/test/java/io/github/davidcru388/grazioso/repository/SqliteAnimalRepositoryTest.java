/*
 * File:     SqliteAnimalRepositoryTest.java
 * Project:  CS-499-Grazioso
 * Author:   David Cruz
 * Date:     2026-11-24
 * Version:  1.0
 *
 * Purpose:
 *   Tests SQLite repository persistence, mapping, and reservation updates.
 *
 * Design notes:
 *   Each test uses a temporary database file so persistent data does not
 *   affect other tests or the working project.
 */

package io.github.davidcru388.grazioso.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import io.github.davidcru388.grazioso.model.AnimalType;
import io.github.davidcru388.grazioso.model.Dog;
import io.github.davidcru388.grazioso.model.Gender;
import io.github.davidcru388.grazioso.model.Monkey;
import io.github.davidcru388.grazioso.model.MonkeySpecies;
import io.github.davidcru388.grazioso.model.RescueAnimal;
import io.github.davidcru388.grazioso.model.TrainingStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class SqliteAnimalRepositoryTest {
    @TempDir
    Path tempDir;

    private SqliteAnimalRepository newRepository(String databaseName) {
        return new SqliteAnimalRepository("jdbc:sqlite:" + tempDir.resolve(databaseName));
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

    private Monkey monkey() {
        return new Monkey(
                "Kylo",
                Gender.MALE,
                1,
                25.6,
                LocalDate.of(2019, 5, 12),
                "United States",
                TrainingStatus.PHASE_I,
                false,
                "United States",
                MonkeySpecies.MARMOSET,
                6.5,
                20.1,
                25.4);
    }

    @Test
    void savesAndMapsDogAndMonkey() {
        SqliteAnimalRepository repository = newRepository("animals.db");
        Dog dog = (Dog) repository.save(dog("Rex", TrainingStatus.PHASE_I));
        Monkey monkey = (Monkey) repository.save(monkey());

        Dog loadedDog = (Dog) repository.findById(dog.getId()).orElseThrow();
        Monkey loadedMonkey = (Monkey) repository.findById(monkey.getId()).orElseThrow();

        assertTrue(dog.getId() > 0);
        assertEquals(AnimalType.DOG, loadedDog.getType());
        assertEquals("Great Dane", loadedDog.getBreed());
        assertEquals(LocalDate.of(2020, 2, 3), loadedDog.getAcquisitionDate());
        assertEquals(MonkeySpecies.MARMOSET, loadedMonkey.getSpecies());
        assertEquals(6.5, loadedMonkey.getTailLengthIn(), 0.0001);
        assertEquals(20.1, loadedMonkey.getHeightIn(), 0.0001);
        assertEquals(25.4, loadedMonkey.getBodyLengthIn(), 0.0001);
    }

    @Test
    void findsNamesWithoutCaseAndWhitespaceDifferencesAndKeepsIdOrder() {
        SqliteAnimalRepository repository = newRepository("lookups.db");
        RescueAnimal first = repository.save(dog("Rex", TrainingStatus.PHASE_I));
        RescueAnimal second = repository.save(dog("Spot", TrainingStatus.IN_SERVICE));

        List<RescueAnimal> matches = repository.findByName(" rex ");
        assertEquals(1, matches.size());
        assertEquals(first.getId(), matches.get(0).getId());
        assertEquals(
                List.of(first.getId(), second.getId()),
                repository.findAll().stream().map(RescueAnimal::getId).toList());
        assertEquals(List.of(), repository.findByName(null));
    }

    @Test
    void marksOnlyAnUnreservedInServiceAnimal() {
        SqliteAnimalRepository repository = newRepository("reservation.db");
        RescueAnimal inService = repository.save(dog("Spot", TrainingStatus.IN_SERVICE));
        RescueAnimal inTraining = repository.save(dog("Rex", TrainingStatus.PHASE_I));

        assertTrue(repository.markReserved(inService.getId()));
        assertFalse(repository.markReserved(inService.getId()));
        assertFalse(repository.markReserved(inTraining.getId()));
        assertFalse(repository.markReserved(999));
        assertTrue(repository.findById(inService.getId()).orElseThrow().isReserved());
    }

    @Test
    void initializesExistingDatabaseWithoutDuplicatingLookupRows() {
        SqliteAnimalRepository repository = newRepository("reopen.db");
        repository.save(dog("Rex", TrainingStatus.PHASE_I));

        SqliteAnimalRepository reopened = newRepository("reopen.db");

        assertEquals(1, reopened.findAll().size());
        Optional<RescueAnimal> loaded = reopened.findByName("rex").stream().findFirst();
        assertTrue(loaded.isPresent());
        assertEquals(TrainingStatus.PHASE_I, loaded.orElseThrow().getTrainingStatus());
    }
}