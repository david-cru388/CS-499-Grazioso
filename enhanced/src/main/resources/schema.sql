-- Author: David Cruz
-- Date: 2026-11-23
-- Version: 1
-- Purpose: Defines the SQLite schema for rescue animals and enum lookups.

CREATE TABLE IF NOT EXISTS training_status (
    id          INTEGER PRIMARY KEY,
    status_name TEXT NOT NULL UNIQUE
);

INSERT OR IGNORE INTO training_status (id, status_name) VALUES
    (1, 'INTAKE'),
    (2, 'PHASE_I'),
    (3, 'PHASE_II'),
    (4, 'PHASE_III'),
    (5, 'PHASE_IV'),
    (6, 'PHASE_V'),
    (7, 'IN_SERVICE');

CREATE TABLE IF NOT EXISTS monkey_species (
    id          INTEGER PRIMARY KEY,
    species_name TEXT NOT NULL UNIQUE
);

INSERT OR IGNORE INTO monkey_species (id, species_name) VALUES
    (1, 'MARMOSET'),
    (2, 'GUENON'),
    (3, 'SQUIRREL_MONKEY'),
    (4, 'CAPUCHIN'),
    (5, 'MACAQUE'),
    (6, 'TAMARIN');

CREATE TABLE IF NOT EXISTS animal (
    id                 INTEGER PRIMARY KEY,
    animal_type        TEXT NOT NULL CHECK (animal_type IN ('DOG', 'MONKEY')),
    name               TEXT NOT NULL,
    gender             TEXT NOT NULL CHECK (gender IN ('MALE', 'FEMALE')),
    age_years          INTEGER NOT NULL CHECK (age_years BETWEEN 0 AND 30),
    weight_lb          REAL NOT NULL CHECK (
                           (animal_type = 'DOG' AND weight_lb BETWEEN 1 AND 250)
                           OR
                           (animal_type = 'MONKEY' AND weight_lb BETWEEN 0.1 AND 60)
                       ),
    acquisition_date   TEXT NOT NULL CHECK (
                           length(acquisition_date) = 10
                           AND date(acquisition_date) = acquisition_date
                       ),
    acquisition_country TEXT NOT NULL,
    training_status_id INTEGER NOT NULL REFERENCES training_status(id),
    reserved           INTEGER NOT NULL CHECK (reserved IN (0, 1)),
    in_service_country TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS dog (
    animal_id INTEGER PRIMARY KEY REFERENCES animal(id) ON DELETE CASCADE,
    breed     TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS monkey (
    animal_id       INTEGER PRIMARY KEY REFERENCES animal(id) ON DELETE CASCADE,
    species_id      INTEGER NOT NULL REFERENCES monkey_species(id),
    tail_length_in  REAL NOT NULL CHECK (tail_length_in BETWEEN 1 AND 40),
    height_in       REAL NOT NULL CHECK (height_in BETWEEN 1 AND 48),
    body_length_in  REAL NOT NULL CHECK (body_length_in BETWEEN 1 AND 40)
);

CREATE INDEX IF NOT EXISTS idx_animal_name ON animal (lower(name));
CREATE INDEX IF NOT EXISTS idx_animal_available
    ON animal (training_status_id, reserved);

PRAGMA user_version = 1;