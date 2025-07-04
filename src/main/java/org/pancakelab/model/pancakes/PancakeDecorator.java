package org.pancakelab.model.pancakes;

import java.util.List;
import java.util.UUID;

public abstract class PancakeDecorator implements PancakeRecipe {
    protected PancakeRecipe decoratedPancake;

    public PancakeDecorator(PancakeRecipe decoratedPancake) {
        this.decoratedPancake = decoratedPancake;
    }

    @Override
    public UUID getOrderId() {
        return decoratedPancake.getOrderId();
    }

    @Override
    public void setOrderId(UUID orderId) {
        decoratedPancake.setOrderId(orderId);
    }

    @Override
    public List<String> ingredients() {
        return decoratedPancake.ingredients();
    }
}