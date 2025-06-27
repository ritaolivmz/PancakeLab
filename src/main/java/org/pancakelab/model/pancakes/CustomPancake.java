package org.pancakelab.model.pancakes;

import java.util.List;
import java.util.Objects;
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

    @Override
    public String getRecipeKey() {
        return "custom";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomPancake that = (CustomPancake) o;
        return Objects.equals(orderId, that.orderId) &&
                Objects.equals(customIngredients, that.customIngredients);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, customIngredients);
    }
}