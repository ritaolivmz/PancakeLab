package org.pancakelab.service;

import org.pancakelab.factory.PancakeFactory;
import org.pancakelab.model.Order;
import org.pancakelab.model.OrderStatus;
import org.pancakelab.model.pancakes.PancakeRecipe;
import org.pancakelab.validators.InputValidator;

import java.util.*;

public class PancakeOrderService {

    private final PancakeService pancakeService;
    private final OrderService orderService;

    PancakeOrderService(PancakeService pancakeService, OrderService orderService) {
        this.pancakeService = pancakeService;
        this.orderService = orderService;
    }

    void removePancakesFromOrder(UUID orderId, String description, int count) {
        InputValidator.validateOrderId(orderId);
        List<PancakeRecipe> removedPancakes = pancakeService.removePancakes(orderId, description, count);
        Optional<Order> orderOptional = orderService.getOrder(orderId);
        Order order = orderOptional.orElseThrow(() -> new NoSuchElementException("Order with ID " + orderId + " not found after pancake removal attempt."));
        OrderLog.logRemovePancakes(order, description, removedPancakes.size(), pancakeService.getPancakes());
    }

    void cancelOrder(UUID orderId) {
        InputValidator.validateOrderId(orderId);
        Optional<Order> orderOptional = orderService.getOrder(orderId);
        Order order = orderOptional.orElseThrow(() -> new NoSuchElementException("Order with ID " + orderId + " not found when cancelling order."));

        if (order.getStatus() == OrderStatus.COMPLETED) {
            throw new IllegalStateException("Cannot cancel an already completed order " + orderId);
        }

        OrderLog.logCancelOrder(order, this.pancakeService.getPancakes());

        pancakeService.removePancakesForOrder(orderId);
        orderService.removeOrder(orderId);
    }

    List<String> viewOrder(UUID orderId) {
        InputValidator.validateOrderId(orderId);
        return pancakeService.getPancakes().stream()
                .filter(pancake -> pancake.getOrderId() != null && pancake.getOrderId().equals(orderId))
                .map(PancakeRecipe::description).toList();
    }

    void addPancakesToOrder(UUID orderId, List<String> ingredients, int count) {
        InputValidator.validateOrderId(orderId);
        Order order = orderService.getOrder(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (order.getStatus() == OrderStatus.COMPLETED) {
            throw new IllegalStateException("Cannot add pancakes to a completed order " + orderId);
        }

        for (int i = 0; i < count; i++) {
            PancakeRecipe pancake = PancakeFactory.createPancake(ingredients);
            pancake.setOrderId(orderId);
            pancakeService.addPancake(pancake);
            OrderLog.logAddPancake(order, pancake.description(), pancakeService.getPancakes());
        }
    }

    Object[] deliverOrder(UUID orderId) {
        InputValidator.validateOrderId(orderId);
        Order order = orderService.getOrder(orderId).orElseThrow(() -> new NoSuchElementException("Order with ID " + orderId + " not found when delivering order."));

        if (order.getStatus() != OrderStatus.PREPARED) {
            throw new IllegalStateException("Order " + orderId + " is not prepared and cannot be delivered.");
        }

        List<String> pancakesToDeliver = pancakeService.getPancakeDescriptionsForOrder(orderId);
        OrderLog.logDeliverOrder(order, pancakeService.getPancakes());
        pancakeService.removePancakesForOrder(orderId);
        orderService.removeOrder(orderId);

        return new Object[] {order, pancakesToDeliver};
    }
}