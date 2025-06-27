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

    public List<String> findDescriptionsByOrderId(UUID orderId) {
        return pancakes.stream()
                .filter(p -> orderId.equals(p.getOrderId()))
                .map(PancakeRecipe::description)
                .toList();
    }

    public void deletePancakesByOrderId(UUID orderId) {
        pancakes.removeIf(p -> orderId.equals(p.getOrderId()));
    }

    public int removeMatching(UUID orderId, String description, int count) {
        List<PancakeRecipe> pancakesToRemove = pancakes.stream()
                .filter(p -> p.getOrderId().equals(orderId) &&
                        p.description().equals(description))
                .limit(count)
                .toList();

        pancakes.removeAll(pancakesToRemove);
        return pancakesToRemove.size();
    }

    public boolean removePancakes(List<PancakeRecipe> pancakesToRemove) {
        pancakesToRemove.forEach(pancake -> this.pancakes.remove(pancake));
        //todo fix this return
        return true;
    }

    public boolean removePancakesForOrder(UUID orderId) {
        return pancakes.removeIf(pancake -> pancake.getOrderId().equals(orderId));
    }
}
