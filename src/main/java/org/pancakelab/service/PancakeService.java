package org.pancakelab.service;

import org.pancakelab.factory.PancakeFactory;
import org.pancakelab.model.pancakes.PancakeRecipe;
import org.pancakelab.repository.PancakeRepository;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class PancakeService {

    private final PancakeRepository pancakeRepo;

    public PancakeService(PancakeRepository pancakeRepo) { // Constructor for dependency injection
        this.pancakeRepo = pancakeRepo;
    }

    public void addPancake(List<String> ingredients) {
        PancakeRecipe pancake = PancakeFactory.createPancake(ingredients);
        pancakeRepo.save(pancake);
    }

    public void addPancake(PancakeRecipe pancake) {
        pancakeRepo.save(pancake);
    }

    public List<PancakeRecipe> getPancakes() {
        return pancakeRepo.retrievePancakes();
    }

    public List<String> getPancakeDescriptionsForOrder(UUID orderId) {
        return pancakeRepo.retrievePancakes().stream()
                .filter(p -> p.getOrderId().equals(orderId))
                .map(PancakeRecipe::description)
                .toList();
    }

    public List<PancakeRecipe> removePancakes(String description, UUID orderId, int count) {
        final AtomicInteger removedCount = new AtomicInteger(0);
        List<PancakeRecipe> pancakesToRemove = getPancakes().stream()
                .filter(pancake -> pancake.getOrderId().equals(orderId) &&
                        pancake.description().equals(description))
                .limit(count)
                .toList();

        pancakeRepo.removePancakes(pancakesToRemove);

        return pancakesToRemove;
    }

    public void removePancakesForOrder(UUID orderId) {
        pancakeRepo.removePancakesForOrder(orderId);
    }
}
