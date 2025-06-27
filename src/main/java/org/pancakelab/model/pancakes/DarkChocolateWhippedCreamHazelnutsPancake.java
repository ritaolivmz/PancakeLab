package org.pancakelab.model.pancakes;

import org.pancakelab.util.PancakeUtils;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class DarkChocolateWhippedCreamHazelnutsPancake extends DarkChocolateWhippedCreamPancake {
    private UUID orderId;

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
        return List.of("dark chocolate", "mustard", "whipped cream", "hazelnuts");
    }

    @Override
    public String getRecipeKey() {
        return PancakeUtils.normalizeIngredients(ingredients());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DarkChocolateWhippedCreamHazelnutsPancake that = (DarkChocolateWhippedCreamHazelnutsPancake) o;
        return Objects.equals(orderId, that.getOrderId()) &&
                Objects.equals(description(), that.description());
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, description());
    }
}
