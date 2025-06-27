package org.pancakelab.factory;

import org.pancakelab.model.pancakes.*;
import org.pancakelab.util.PancakeUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PancakeFactory {

    private static final Map<String, PancakeRecipe> existingRecipes = new HashMap<>();

    static {
        addExistingRecipe(new DarkChocolatePancake());
        addExistingRecipe(new DarkChocolateWhippedCreamPancake());
        addExistingRecipe(new DarkChocolateWhippedCreamHazelnutsPancake());
        addExistingRecipe(new MilkChocolatePancake());
        addExistingRecipe(new MilkChocolateHazelnutsPancake());
    }

    private static void addExistingRecipe(PancakeRecipe recipe) {
        existingRecipes.put(recipe.getRecipeKey(), recipe);
    }

    public static PancakeRecipe createPancake(List<String> ingredients) {
        String key =  PancakeUtils.normalizeIngredients(ingredients);
        return existingRecipes.getOrDefault(key, new CustomPancake(ingredients));
    }
}

//todo investigate if decorator is a better choice
/*
public class PancakeFactory {
    public static PancakeRecipe createPancake(List<String> ingredients) {
        PancakeRecipe base = new PlainPancake();
        for (String ingredient : ingredients) {
            switch (ingredient.toLowerCase()) {
                case "milk-chocolate" -> base = new MilkChocolateDecorator(base);
                case "dark-chocolate" -> base = new DarkChocolateDecorator(base);
                case "whipped-cream" -> base = new WhippedCreamDecorator(base);
                case "mustard" -> base = new MustardDecorator(base);  // even if cursed
                default -> throw new IllegalArgumentException("Unknown ingredient: " + ingredient);
            }
        }
        return base;
    }
}
*/