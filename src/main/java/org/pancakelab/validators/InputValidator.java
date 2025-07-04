package org.pancakelab.validators;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class InputValidator {

    public synchronized static void validateNonNegativeNumber(int... numbers) {
        if (numbers == null) {
            throw new IllegalArgumentException("Numbers should contain at least one object");
        }
        boolean anyNegative = Arrays.stream(numbers)
                .anyMatch(number -> number < 0);

        if (anyNegative) {
            throw new IllegalArgumentException("All numbers must be non-negative.");
        }
    }

    public synchronized static void validatePositiveNumber(int... numbers) {
        if (numbers == null) {
            throw new IllegalArgumentException("Numbers should contain at least one object");
        }
        boolean anyNegative = Arrays.stream(numbers)
                .anyMatch(number -> number <= 0);

        if (anyNegative) {
            throw new IllegalArgumentException("All numbers must be positive.");
        }
    }

    public static void validateOrderId(UUID orderId) {
        if (orderId == null) {
            throw new IllegalArgumentException("The provided order ID can not be null.");
        }
    }

    public static void validateIsNotNull(Object object) {
        if (object == null) {
            throw new IllegalArgumentException("The provided object can not be null.");
        }
    }

    public static void validateIsNotEmpty(List list) {
        if (list == null) {
            throw new IllegalArgumentException("The provided list can not be null.");
        }
        if  (list.isEmpty()) {
            throw new IllegalArgumentException("The provided list can not be empty.");
        }
    }

}
