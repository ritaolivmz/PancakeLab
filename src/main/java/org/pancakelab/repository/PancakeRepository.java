package org.pancakelab.repository;

import org.pancakelab.model.pancakes.PancakeRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class PancakeRepository {

    private final List<PancakeRecipe> pancakes = new CopyOnWriteArrayList<>();

    public void save(PancakeRecipe pancake) {
        pancakes.add(pancake);
    }

    public List<PancakeRecipe> retrievePancakes() {
        return new ArrayList<>(pancakes);
    }

    public void removePancakes(List<PancakeRecipe> pancakesToRemove) {
        pancakes.removeAll(pancakesToRemove);
    }

    public void removePancakesForOrder(UUID orderId) {
        pancakes.removeIf(pancake -> pancake.getOrderId() != null && pancake.getOrderId().equals(orderId));
    }
}