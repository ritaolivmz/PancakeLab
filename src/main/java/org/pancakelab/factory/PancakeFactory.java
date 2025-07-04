package org.pancakelab.factory;

import org.pancakelab.model.pancakes.*;

import java.util.*;

public class PancakeFactory {

    public static PancakeRecipe createPancake(List<String> ingredients) {
        PancakeRecipe pancake = new BasePancake();

        List<IngredientsType> decoratorTypes = new ArrayList<>();
        List<String> customIngredients = new ArrayList<>();

        for (String ingredient : ingredients) {
            Optional<IngredientsType> decoratorType = IngredientsType.fromIngredientName(ingredient);
            if (decoratorType.isPresent()) {
                decoratorTypes.add(decoratorType.get());
            } else {
                customIngredients.add(ingredient);
            }
        }

        if (!customIngredients.isEmpty()) {
            pancake = new CustomPancake(customIngredients);
        }

        for (IngredientsType type : decoratorTypes) {
            switch (type) {
                case MILK_CHOCOLATE:
                    pancake = new MilkChocolatePancakeDecorator(pancake);
                    break;
                case DARK_CHOCOLATE:
                    pancake = new DarkChocolatePancakeDecorator(pancake);
                    break;
                case WHIPPED_CREAM:
                    pancake = new WhippedCreamPancakeDecorator(pancake);
                    break;
                case HAZELNUTS:
                    pancake = new HazelnutsPancakeDecorator(pancake);
                    break;
            }
        }
        return pancake;
    }
}