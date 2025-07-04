package org.pancakelab.model.pancakes;

import java.util.ArrayList;
import java.util.List;

public class DarkChocolatePancakeDecorator extends PancakeDecorator {

    public DarkChocolatePancakeDecorator(PancakeRecipe pancakeRecipe) {
        super(pancakeRecipe);
    }

    @Override
    public List<String> ingredients() {
        List<String> ingredients = new ArrayList<>(List.of(IngredientsType.DARK_CHOCOLATE.name()));
        ingredients.addAll(decoratedPancake.ingredients());
        return ingredients;
    }
}
