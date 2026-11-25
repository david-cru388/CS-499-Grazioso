/*
 * File:     SqliteAnimalRepository.java
 * Project:  CS-499-Grazioso
 * Author:   David Cruz
 * Date:     2026-11-24
 * Version:  1.0
 *
 * Purpose:
 *   Persists and retrieves rescue animals using SQLite.
 *
 * Design notes:
 *   Implements AnimalRepository so the service can use SQLite without
 *   depending on JDBC or changing its behavior.
 */

package io.github.davidcru388.grazioso.repository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import io.github.davidcru388.grazioso.model.AnimalType;
import io.github.davidcru388.grazioso.model.Dog;
import io.github.davidcru388.grazioso.model.Gender;
import io.github.davidcru388.grazioso.model.Monkey;
import io.github.davidcru388.grazioso.model.MonkeySpecies;
import io.github.davidcru388.grazioso.model.RescueAnimal;
import io.github.davidcru388.grazioso.model.TrainingStatus;

public class SqliteAnimalRepository implements AnimalRepository {
    private static final String BASE_SELECT = """
            SELECT a.id, a.animal_type, a.name, a.gender, a.age_years, a.weight_lb,
                   a.acquisition_date, a.acquisition_country, ts.status_name,
                   a.reserved, a.in_service_country, d.breed, ms.species_name,
                   m.tail_length_in, m.height_in, m.body_length_in
            FROM animal a
            JOIN training_status ts ON ts.id = a.training_status_id
            LEFT JOIN dog d ON d.animal_id = a.id
            LEFT JOIN monkey m ON m.animal_id = a.id
            LEFT JOIN monkey_species ms ON ms.id = m.species_id
            """;

    private final String jdbcUrl;

    public SqliteAnimalRepository(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
        initializeSchema();
    }

    private Connection connect() throws SQLException {
        Connection connection = DriverManager.getConnection(jdbcUrl);
        try (Statement statement = connection.createStatement()) {
            // SQLite disables foreign-key enforcement by default, and this setting
            // applies per connection, so enable it every time a connection is opened.
            statement.execute("PRAGMA foreign_keys = ON");
            return connection;
        } catch (SQLException e) {
            // Close the connection before rethrowing so failed setup does not leave it open.
            connection.close();
            throw e;
        }
    }

    private void initializeSchema() {
        String schema;
        try (InputStream stream = getClass().getResourceAsStream("/schema.sql")) {
            if (stream == null) {
                throw new RepositoryException(
                        "Could not load the database schema.",
                        new IOException("Classpath resource /schema.sql was not found."));
            }
            try (BufferedReader reader =
                    new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
                schema = reader.lines()
                        .filter(line -> !line.stripLeading().startsWith("--"))
                        .collect(Collectors.joining("\n"));
            }
        } catch (IOException e) {
            throw new RepositoryException("Could not read the database schema.", e);
        }

        // Remove full-line comments before splitting; schema SQL must not contain
        // semicolons inside string values.
        try (Connection connection = connect(); Statement statement = connection.createStatement()) {
            for (String sql : schema.split(";")) {
                if (!sql.isBlank()) {
                    statement.execute(sql);
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Could not initialize the database schema.", e);
        }
    }

    @Override
    public RescueAnimal save(RescueAnimal animal) {
        // Check before writing because a committed row cannot be undone by ID assignment.
        if (animal.getId() != 0) {
            throw new IllegalStateException("Animal has already been saved.");
        }

        // A missing enum lookup yields NULL and the NOT NULL constraint rejects the insert.
        String insertAnimal = """
                INSERT INTO animal (
                    animal_type, name, gender, age_years, weight_lb, acquisition_date,
                    acquisition_country, training_status_id, reserved, in_service_country
                ) VALUES (?, ?, ?, ?, ?, ?, ?, (SELECT id FROM training_status WHERE status_name = ?), ?, ?)
                """;

        try (Connection connection = connect()) {
            try {
                connection.setAutoCommit(false);
                long newId;
                try (PreparedStatement statement = connection.prepareStatement(
                        insertAnimal, Statement.RETURN_GENERATED_KEYS)) {
                    statement.setString(1, animal.getType().name());
                    statement.setString(2, animal.getName());
                    statement.setString(3, animal.getGender().name());
                    statement.setInt(4, animal.getAgeYears());
                    statement.setDouble(5, animal.getWeightLb());
                    statement.setString(6, animal.getAcquisitionDate().toString());
                    statement.setString(7, animal.getAcquisitionCountry());
                    statement.setString(8, animal.getTrainingStatus().name());
                    statement.setInt(9, animal.isReserved() ? 1 : 0);
                    statement.setString(10, animal.getInServiceCountry());
                    statement.executeUpdate();

                    try (ResultSet keys = statement.getGeneratedKeys()) {
                        if (!keys.next()) {
                            throw new SQLException("The insert did not return a generated ID.");
                        }
                        newId = keys.getLong(1);
                    }
                }

                if (animal instanceof Dog dog) {
                    try (PreparedStatement statement = connection.prepareStatement(
                            "INSERT INTO dog (animal_id, breed) VALUES (?, ?)")) {
                        statement.setLong(1, newId);
                        statement.setString(2, dog.getBreed());
                        statement.executeUpdate();
                    }
                } else if (animal instanceof Monkey monkey) {
                    try (PreparedStatement statement = connection.prepareStatement("""
                            INSERT INTO monkey (
                                animal_id, species_id, tail_length_in, height_in, body_length_in
                            ) VALUES (?, (SELECT id FROM monkey_species WHERE species_name = ?), ?, ?, ?)
                            """)) {
                        statement.setLong(1, newId);
                        statement.setString(2, monkey.getSpecies().name());
                        statement.setDouble(3, monkey.getTailLengthIn());
                        statement.setDouble(4, monkey.getHeightIn());
                        statement.setDouble(5, monkey.getBodyLengthIn());
                        statement.executeUpdate();
                    }
                } else {
                    throw new SQLException("Unsupported rescue animal subtype.");
                }

                connection.commit();
                // Assign only after both rows are safely committed.
                animal.assignId(newId);
                return animal;
            } catch (SQLException e) {
                // Roll back so a failed subtype insert cannot leave an animal row alone.
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RepositoryException("Could not save the animal.", e);
        }
    }

    @Override
    public boolean markReserved(long id) {
        // Check and update in one statement so status cannot change between them.
        String sql = """
                UPDATE animal
                SET reserved = 1
                WHERE id = ?
                  AND reserved = 0
                  AND training_status_id = (
                      SELECT id FROM training_status WHERE status_name = 'IN_SERVICE'
                  )
                """;
        try (Connection connection = connect();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            return statement.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RepositoryException("Could not reserve the animal.", e);
        }
    }

    @Override
    public Optional<RescueAnimal> findById(long id) {
        String sql = BASE_SELECT + " WHERE a.id = ?";
        try (Connection connection = connect();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet results = statement.executeQuery()) {
                return results.next() ? Optional.of(mapRow(results)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RepositoryException("Could not find the animal.", e);
        }
    }

    @Override
    public List<RescueAnimal> findByName(String name) {
        if (name == null) {
            return List.of();
        }

        String sql = BASE_SELECT + " WHERE lower(a.name) = ?";
        try (Connection connection = connect();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name.strip().toLowerCase(Locale.ROOT));
            try (ResultSet results = statement.executeQuery()) {
                List<RescueAnimal> animals = new ArrayList<>();
                while (results.next()) {
                    animals.add(mapRow(results));
                }
                return List.copyOf(animals);
            }
        } catch (SQLException e) {
            throw new RepositoryException("Could not find animals by name.", e);
        }
    }

    @Override
    public List<RescueAnimal> findAll() {
        String sql = BASE_SELECT + " ORDER BY a.id";
        try (Connection connection = connect();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet results = statement.executeQuery()) {
            List<RescueAnimal> animals = new ArrayList<>();
            while (results.next()) {
                animals.add(mapRow(results));
            }
            return List.copyOf(animals);
        } catch (SQLException e) {
            throw new RepositoryException("Could not load animals.", e);
        }
    }

    private RescueAnimal mapRow(ResultSet results) throws SQLException {
        AnimalType type = AnimalType.valueOf(results.getString("animal_type"));
        Gender gender = Gender.valueOf(results.getString("gender"));
        TrainingStatus status = TrainingStatus.valueOf(results.getString("status_name"));
        LocalDate acquisitionDate = LocalDate.parse(results.getString("acquisition_date"));
        boolean reserved = results.getInt("reserved") == 1;

        // Reuse validating constructors so invalid hand-edited rows are rejected on load.
        RescueAnimal animal;
        if (type == AnimalType.DOG) {
            animal = new Dog(
                    results.getString("name"),
                    gender,
                    results.getInt("age_years"),
                    results.getDouble("weight_lb"),
                    acquisitionDate,
                    results.getString("acquisition_country"),
                    status,
                    reserved,
                    results.getString("in_service_country"),
                    results.getString("breed"));
        } else {
            animal = new Monkey(
                    results.getString("name"),
                    gender,
                    results.getInt("age_years"),
                    results.getDouble("weight_lb"),
                    acquisitionDate,
                    results.getString("acquisition_country"),
                    status,
                    reserved,
                    results.getString("in_service_country"),
                    MonkeySpecies.valueOf(results.getString("species_name")),
                    results.getDouble("tail_length_in"),
                    results.getDouble("height_in"),
                    results.getDouble("body_length_in"));
        }
        animal.assignId(results.getLong("id"));
        return animal;
    }
}