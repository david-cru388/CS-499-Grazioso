/*
 * File:     DogTest.java
 * Project:  CS-499-Grazioso
 * Author:   David Cruz
 * Date:     2026-11-13
 * Version:  1.0
 *
 * Purpose:
 *   Tests that Dog validates its own weight limits and other model constraints.
 *
 * Design notes:
 *   Constructs valid Dog instances and varies one input at a time to verify
 *   validation at the model boundary.
 */

package io.github.davidcru388.grazioso.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import io.github.davidcru388.grazioso.validation.ValidationException;
import org.junit.jupiter.api.Test;

class DogTest {

        private Dog dogWithWeight(double weightLb) {
                return new Dog(
                                "Fido",
                                Gender.MALE,
                                3,
                                weightLb,
                                LocalDate.of(2020, 2, 3),
                                "United States",
                                TrainingStatus.PHASE_I,
                                false,
                                "United States",
                                "Golden Retriever");
        }

        @Test
        void acceptsWeightAtBothLimits() {
                assertEquals(Dog.MIN_WEIGHT_LB, dogWithWeight(Dog.MIN_WEIGHT_LB).getWeightLb(), 0.0001);
                assertEquals(Dog.MAX_WEIGHT_LB, dogWithWeight(Dog.MAX_WEIGHT_LB).getWeightLb(), 0.0001);
        }

        @Test
        void rejectsWeightJustOutsideLimits() {
                assertThrows(ValidationException.class,
                                () -> dogWithWeight(Dog.MIN_WEIGHT_LB - 0.1));
                assertThrows(ValidationException.class,
                                () -> dogWithWeight(Dog.MAX_WEIGHT_LB + 0.1));
        }

        @Test
        void rejectsBreedContainingDigits() {
                assertThrows(ValidationException.class, () -> new Dog(
                                "Rex",
                                Gender.MALE,
                                3,
                                35.2,
                                LocalDate.of(2020, 2, 3),
                                "United States",
                                TrainingStatus.PHASE_I,
                                false,
                                "United States",
                                "Lab123"));
        }

        @Test
        void rejectsFutureAcquisitionDate() {
                assertThrows(ValidationException.class, () -> new Dog(
                                "Rex",
                                Gender.MALE,
                                3,
                                35.2,
                                LocalDate.now().plusDays(1),
                                "United States",
                                TrainingStatus.PHASE_I,
                                false,
                                "United States",
                                "Great Dane"));
        }

        @Test
        void rejectsAssigningIdMoreThanOnce() {
                Dog dog = dogWithWeight(35.2);
                dog.assignId(1);
                assertThrows(IllegalStateException.class, () -> dog.assignId(2));
        }
}
