package org.pancakelab.model.pancakes;

import java.util.List;
import java.util.UUID;

public class CustomPancake implements PancakeRecipe {
    private UUID orderId;
    private final List<String> customIngredients;

    public CustomPancake(List<String> customIngredients) {
        this.customIngredients = customIngredients;
    }

    @Override
    public UUID getOrderId() {
        return orderId;
    }

    @Override
    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    @Override
    public List<String> ingredients() {
        return customIngredients;
    }
}