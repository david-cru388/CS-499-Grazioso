/*
 * File:     ConsoleMenu.java
 * Project:  CS-499-Grazioso
 * Author:   David Cruz
 * Date:     2026-11-14
 * Version:  1.0
 *
 * Purpose:
 *   Provides the console interface for managing rescue animals.
 *
 * Design notes:
 *   Improved menu by Receiving input and output streams through its constructor so the menu can
 *   be tested without depending on System.in and System.out.
 * 
 */

package io.github.davidcru388.grazioso.ui;

import java.io.PrintStream;
import java.util.Scanner;
import java.util.function.Function;

import io.github.davidcru388.grazioso.model.Dog;
import io.github.davidcru388.grazioso.model.Gender;
import io.github.davidcru388.grazioso.model.RescueAnimal;
import io.github.davidcru388.grazioso.model.TrainingStatus;
import io.github.davidcru388.grazioso.service.RescueAnimalService;
import io.github.davidcru388.grazioso.validation.InputValidator;
import io.github.davidcru388.grazioso.validation.ValidationException;

public class ConsoleMenu {
    private final RescueAnimalService service;
    private final Scanner in;
    private final PrintStream out;

    public ConsoleMenu(RescueAnimalService service, Scanner in, PrintStream out) {
        this.service = service;
        this.in = in;
        this.out = out;
    }

    public void run() {
        while (true) {
            out.println();
            out.println("Rescue Animal System");
            out.println("1. Add a dog");
            out.println("2. List all animals");
            out.println("3. List available animals");
            out.println("4. Reserve an animal");
            out.println("5. Quit");

            int choice = prompt("Choice", s -> InputValidator.parseMenuChoice(s, 5));
            switch (choice) {
                case 1 -> addDog();
                case 2 -> listAnimals(service.findAll());
                case 3 -> listAnimals(service.availableAnimals());
                case 4 -> reserveAnimal();
                case 5 -> {
                    return;
                }
                default -> throw new IllegalStateException("Unexpected menu choice: " + choice);
            }
        }
    }

    private void addDog() {
        String name = prompt("Name", InputValidator::validateName);
        String breed = prompt("Breed", s -> InputValidator.validateText(s, "Breed"));
        Gender gender = promptGender();
        int ageYears = prompt("Age", InputValidator::parseAge);
        double weightLb = prompt(
                "Weight (lb)",
                s -> InputValidator.parseMeasurement(
                        s, "Weight", Dog.MIN_WEIGHT_LB, Dog.MAX_WEIGHT_LB));
        var acquisitionDate = prompt("Acquisition date (yyyy-MM-dd)", InputValidator::parseDate);
        String acquisitionCountry = prompt("Acquisition country",
                s -> InputValidator.validateText(s, "Acquisition country"));
        TrainingStatus trainingStatus = promptTrainingStatus();
        String inServiceCountry = prompt("In-service country",
                s -> InputValidator.validateText(s, "In-service country"));

        try {
            // Newly entered animals are not reserved.
            Dog dog = new Dog(
                    name,
                    gender,
                    ageYears,
                    weightLb,
                    acquisitionDate,
                    acquisitionCountry,
                    trainingStatus,
                    false,
                    inServiceCountry,
                    breed);
            service.intake(dog);
            out.println("Dog added with ID " + dog.getId() + ".");
        } catch (ValidationException e) {
            out.println(e.getMessage());
        }
    }

    private Gender promptGender() {
        Gender[] values = Gender.values();
        out.println("Gender:");
        for (int i = 0; i < values.length; i++) {
            out.println((i + 1) + ". " + values[i].getDisplayName());
        }
        int choice = prompt("Gender choice", s -> InputValidator.parseMenuChoice(s, values.length));
        return values[choice - 1];
    }

    private TrainingStatus promptTrainingStatus() {
        TrainingStatus[] values = TrainingStatus.values();
        out.println("Training status:");
        for (int i = 0; i < values.length; i++) {
            out.println((i + 1) + ". " + values[i].getDisplayName());
        }
        int choice = prompt(
                "Training status choice",
                s -> InputValidator.parseMenuChoice(s, values.length));
        return values[choice - 1];
    }

    private void listAnimals(Iterable<RescueAnimal> animals) {
        for (RescueAnimal animal : animals) {
            out.println(
                    "ID: " + animal.getId()
                            + ", Type: " + animal.getType().getDisplayName()
                            + ", Name: " + animal.getName()
                            + ", Training status: " + animal.getTrainingStatus().getDisplayName()
                            + ", Reserved: " + (animal.isReserved() ? "yes" : "no"));
        }
    }

    private void reserveAnimal() {
        int id = prompt("Animal ID", InputValidator::parseId);
        try {
            service.reserve(id);
            out.println("Animal " + id + " reserved.");
        } catch (ValidationException e) {
            out.println(e.getMessage());
        }
    }

    // Repeats until valid input is received, then returns the parsed value.
    // Validation errors are printed to the console.
    private <T> T prompt(String label, Function<String, T> parser) {
        while (true) {
            out.print(label + ": ");
            if (!in.hasNextLine()) {
                throw new IllegalStateException("Input ended.");
            }
            try {
                return parser.apply(in.nextLine());
            } catch (ValidationException e) {
                out.println(e.getMessage());
            }
        }
    }
}
