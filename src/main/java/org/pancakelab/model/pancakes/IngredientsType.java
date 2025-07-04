package org.pancakelab.model.pancakes;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum IngredientsType {
    MILK_CHOCOLATE("milk chocolate"),
    DARK_CHOCOLATE("dark chocolate"),
    WHIPPED_CREAM("whipped cream"),
    HAZELNUTS("hazelnuts");

    private final String ingredientName;

    IngredientsType(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    private static final Map<String, IngredientsType> BY_NAME =
            Arrays.stream(values()).collect(Collectors.toMap(IngredientsType::getIngredientName, Function.identity()));

    public static Optional<IngredientsType> fromIngredientName(String name) {
        return Optional.ofNullable(BY_NAME.get(name.toLowerCase().trim()));
    }
}
