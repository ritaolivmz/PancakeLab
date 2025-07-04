package org.pancakelab.model.pancakes;

import java.util.ArrayList;
import java.util.List;

public class HazelnutsPancakeDecorator extends PancakeDecorator {

    public HazelnutsPancakeDecorator(PancakeRecipe pancakeRecipe) {
        super(pancakeRecipe);
    }

    @Override
    public List<String> ingredients() {
        List<String> ingredients = new ArrayList<>(List.of(IngredientsType.HAZELNUTS.name()));
        ingredients.addAll(decoratedPancake.ingredients());
        return ingredients;
    }
}
