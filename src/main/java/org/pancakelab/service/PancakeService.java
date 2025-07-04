package org.pancakelab.service;

import org.pancakelab.factory.PancakeFactory;
import org.pancakelab.model.pancakes.PancakeRecipe;
import org.pancakelab.repository.PancakeRepository;
import org.pancakelab.validators.InputValidator;

import java.util.List;
import java.util.UUID;

public class PancakeService {

    private final PancakeRepository pancakeRepo;

    PancakeService(PancakeRepository pancakeRepo) {
        this.pancakeRepo = pancakeRepo;
    }

    void addPancake(List<String> ingredients) {
        InputValidator.validateIsNotEmpty(ingredients);
        PancakeRecipe pancake = PancakeFactory.createPancake(ingredients);
        pancakeRepo.save(pancake);
    }

    void addPancake(PancakeRecipe pancake) {
        InputValidator.validateIsNotNull(pancake);
        pancakeRepo.save(pancake);
    }

    List<PancakeRecipe> getPancakes() {
        return pancakeRepo.retrievePancakes();
    }

    List<String> getPancakeDescriptionsForOrder(UUID orderId) {
        InputValidator.validateOrderId(orderId);
        return pancakeRepo.retrievePancakes().stream()
                .filter(p -> p.getOrderId().equals(orderId))
                .map(PancakeRecipe::description)
                .toList();
    }

    List<PancakeRecipe> removePancakes(UUID orderId, String description, int count) {
        InputValidator.validateOrderId(orderId);
        InputValidator.validatePositiveNumber(count);
        List<PancakeRecipe> pancakesToRemove = getPancakes().stream()
                .filter(pancake -> pancake.getOrderId().equals(orderId) &&
                        pancake.description().equals(description))
                .limit(count)
                .toList();

        if (!pancakesToRemove.isEmpty()) {
            pancakeRepo.removePancakes(pancakesToRemove);
        }

        return pancakesToRemove;
    }

    void removePancakesForOrder(UUID orderId) {
        InputValidator.validateOrderId(orderId);
        pancakeRepo.removePancakesForOrder(orderId);
    }
}
