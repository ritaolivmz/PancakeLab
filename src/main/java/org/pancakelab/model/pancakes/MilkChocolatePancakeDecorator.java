package org.pancakelab.model.pancakes;

import java.util.ArrayList;
import java.util.List;

public class MilkChocolatePancakeDecorator extends PancakeDecorator {

    public MilkChocolatePancakeDecorator(PancakeRecipe pancakeRecipe) {
        super(pancakeRecipe);
    }

    @Override
    public List<String> ingredients() {
        List<String> ingredients = new ArrayList<>(List.of(IngredientsType.MILK_CHOCOLATE.name()));
        ingredients.addAll(decoratedPancake.ingredients());
        return ingredients;
    }
}
