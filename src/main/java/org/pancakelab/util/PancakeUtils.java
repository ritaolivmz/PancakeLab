package org.pancakelab.util;

import java.util.List;

public class PancakeUtils {

    public static String normalizeIngredients(List<String> ingredients) {
        return ingredients.stream()
                .map(String::toLowerCase)
                .sorted()
                .reduce((a, b) -> a + "_" + b)
                .orElse("custom");
    }

}

