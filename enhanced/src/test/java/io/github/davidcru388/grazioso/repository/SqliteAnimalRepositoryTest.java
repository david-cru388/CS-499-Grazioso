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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
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
        return new SqliteAnimalRepository(databaseUrl(databaseName));
    }

    private String databaseUrl(String databaseName) {
        return "jdbc:sqlite:" + tempDir.resolve(databaseName);
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

    @Test
    void rejectsSavingTheSameAnimalTwiceWithoutAddingAnotherRow() {
        SqliteAnimalRepository repository = newRepository("duplicate.db");
        Dog rex = dog("Rex", TrainingStatus.PHASE_I);
        repository.save(rex);

        assertThrows(IllegalStateException.class, () -> repository.save(rex));
        assertEquals(1, repository.findAll().size());
    }

    @Test
    void treatsSqlSyntaxInNameAsData() {
        SqliteAnimalRepository repository = newRepository("injection.db");
        String injectionLikeName = "Robert' OR true OR 'x";
        RescueAnimal matchingAnimal = repository.save(dog(injectionLikeName, TrainingStatus.PHASE_I));
        repository.save(dog("Other", TrainingStatus.PHASE_I));

        List<RescueAnimal> matches = repository.findByName(injectionLikeName);

        assertEquals(1, matches.size());
        assertEquals(matchingAnimal.getId(), matches.get(0).getId());
        assertEquals(2, repository.findAll().size());
    }

    @Test
    void rollsBackAnimalInsertWhenMonkeyInsertFails() throws SQLException {
        String jdbcUrl = databaseUrl("rollback.db");
        SqliteAnimalRepository repository = new SqliteAnimalRepository(jdbcUrl);
        repository.save(dog("Terry", TrainingStatus.PHASE_I));
        int previousAnimalCount = repository.findAll().size();

        try (Connection connection = DriverManager.getConnection(jdbcUrl);
                Statement statement = connection.createStatement()) {
            statement.executeUpdate(
                    "DELETE FROM monkey_species WHERE species_name = 'MARMOSET'");
        }

        Monkey tamarin = new Monkey(
                "Tami",
                Gender.FEMALE,
                2,
                10.0,
                LocalDate.of(2020, 3, 10),
                "United States",
                TrainingStatus.PHASE_I,
                false,
                "United States",
                MonkeySpecies.MARMOSET,
                10.0,
                20.0,
                18.0);

        assertThrows(RepositoryException.class, () -> repository.save(tamarin));
        assertEquals(previousAnimalCount, repository.findAll().size());
    }

    @Test
    void databaseRejectsAgeOutsideCheckConstraint() throws SQLException {
        String jdbcUrl = databaseUrl("age-check.db");
        new SqliteAnimalRepository(jdbcUrl);
        String sql = """
                INSERT INTO animal (
                    animal_type, name, gender, age_years, weight_lb, acquisition_date,
                    acquisition_country, training_status_id, reserved, in_service_country
                ) VALUES ('DOG', 'Rex', 'MALE', 31, 35.2, '2020-02-03',
                          'United States', 2, 0, 'United States')
                """;

        try (Connection connection = DriverManager.getConnection(jdbcUrl);
                PreparedStatement statement = connection.prepareStatement(sql)) {
            assertThrows(SQLException.class, statement::executeUpdate);
        }
    }
}