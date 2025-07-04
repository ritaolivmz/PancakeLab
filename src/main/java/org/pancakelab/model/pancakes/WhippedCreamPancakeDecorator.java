package org.pancakelab.model.pancakes;

import java.util.ArrayList;
import java.util.List;

public class WhippedCreamPancakeDecorator extends PancakeDecorator {

    public WhippedCreamPancakeDecorator(PancakeRecipe pancakeRecipe) {
        super(pancakeRecipe);
    }

    @Override
    public List<String> ingredients() {
        List<String> ingredients = new ArrayList<>(List.of(IngredientsType.WHIPPED_CREAM.name()));
        ingredients.addAll(decoratedPancake.ingredients());
        return ingredients;
    }
}
