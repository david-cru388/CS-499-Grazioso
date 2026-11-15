/*
 * File:     App.java
 * Project:  CS-499-Grazioso
 * Author:   David Cruz
 * Date:     2026-11-14
 * Version:  1.0
 *
 * Purpose:
 *   Starts the rescue animal application and seeds its sample animals.
 *
 * Design notes:
 *   Creates the repository and service before passing the service to the
 *   console menu.
 * 
 */

package io.github.davidcru388.grazioso.app;

import java.time.LocalDate;
import java.util.Scanner;

import io.github.davidcru388.grazioso.model.Dog;
import io.github.davidcru388.grazioso.model.Gender;
import io.github.davidcru388.grazioso.model.Monkey;
import io.github.davidcru388.grazioso.model.MonkeySpecies;
import io.github.davidcru388.grazioso.model.TrainingStatus;
import io.github.davidcru388.grazioso.repository.InMemoryAnimalRepository;
import io.github.davidcru388.grazioso.service.RescueAnimalService;
import io.github.davidcru388.grazioso.ui.ConsoleMenu;

public class App {
        public static void main(String[] args) {
                RescueAnimalService service = new RescueAnimalService(new InMemoryAnimalRepository());
                seedSampleData(service);
                new ConsoleMenu(service, new Scanner(System.in), System.out).run();
        }

        private static void seedSampleData(RescueAnimalService service) {
                service.intake(new Dog(
                                "Spot",
                                Gender.MALE,
                                1,
                                25.6,
                                LocalDate.of(2019, 5, 12),
                                "United States",
                                TrainingStatus.IN_SERVICE,
                                false,
                                "United States",
                                "German Shepherd"));
                service.intake(new Dog(
                                "Rex",
                                Gender.MALE,
                                3,
                                35.2,
                                LocalDate.of(2020, 2, 3),
                                "United States",
                                TrainingStatus.PHASE_I,
                                false,
                                "United States",
                                "Great Dane"));
                service.intake(new Dog(
                                "Bella",
                                Gender.FEMALE,
                                4,
                                25.6,
                                LocalDate.of(2019, 12, 12),
                                "Canada",
                                TrainingStatus.IN_SERVICE,
                                false,
                                "Canada",
                                "Chihuahua"));

                service.intake(new Monkey(
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
                                25.4));
                service.intake(new Monkey(
                                "Ren",
                                Gender.MALE,
                                3,
                                35.2,
                                LocalDate.of(2020, 2, 3),
                                "United States",
                                TrainingStatus.IN_SERVICE,
                                false,
                                "United States",
                                MonkeySpecies.GUENON,
                                8.5,
                                15.7,
                                23.4));
                service.intake(new Monkey(
                                "Chase",
                                Gender.FEMALE,
                                4,
                                25.6,
                                LocalDate.of(2019, 12, 12),
                                "Canada",
                                TrainingStatus.IN_SERVICE,
                                false,
                                "Canada",
                                MonkeySpecies.SQUIRREL_MONKEY,
                                4.0,
                                13.5,
                                17.5));
        }
}
