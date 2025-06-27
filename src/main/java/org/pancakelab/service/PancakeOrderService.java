package org.pancakelab.service;

import org.pancakelab.factory.PancakeFactory;
import org.pancakelab.model.Order;
import org.pancakelab.model.pancakes.PancakeRecipe;

import java.util.*;

public class PancakeOrderService {

    private final PancakeService pancakeService;
    private final OrderService orderService;

    public PancakeOrderService(PancakeService pancakeService, OrderService orderService) {
        this.pancakeService = pancakeService;
        this.orderService = orderService;
    }

    public void removePancakesFromOrder(String description, UUID orderId, int count) {
        List<PancakeRecipe> removedPancakes = pancakeService.removePancakes(description, orderId, count);
        Optional<Order> orderOptional = orderService.getOrder(orderId);
        Order order = orderOptional.orElseThrow(() -> new NoSuchElementException("Order with ID " + orderId + " not found after pancake removal attempt."));
        OrderLog.logRemovePancakes(order, description, removedPancakes.size(), removedPancakes);
    }

    public void cancelOrder(UUID orderId) {
        OrderLog.logCancelOrder(orderService.getOrder(orderId).orElseThrow(() -> new NoSuchElementException("Order with ID " + orderId + " not found when cancelling order.")), this.pancakeService.getPancakes());

        pancakeService.removePancakesForOrder(orderId);
        orderService.listCompletedOrders().removeIf(o -> o.equals(orderId));
        orderService.listPreparedOrders().removeIf(u -> u.equals(orderId));
        orderService.getOrders().remove(orderId);
    }

    public List<String> viewOrder(UUID orderId) {
        return pancakeService.getPancakes().stream()
                .filter(pancake -> pancake.getOrderId().equals(orderId))
                .map(PancakeRecipe::description).toList();
    }

    public void addPancakesToOrder(UUID orderId, List<String> ingredients, int count) {
        Order order = orderService.getOrder(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        for (int i = 0; i < count; i++) {
            PancakeRecipe pancake = PancakeFactory.createPancake(ingredients);
            pancake.setOrderId(orderId);
            pancakeService.addPancake(pancake);
            OrderLog.logAddPancake(order, pancake.description(), pancakeService.getPancakes());
        }
    }

    public Object[] deliverOrder(UUID orderId) {
        if (!orderService.isPrepared(orderId)) return null;

        Order order = orderService.getOrder(orderId).orElseThrow(() -> new NoSuchElementException("Order with ID " + orderId + " not found when delivering order."));
        List<String> pancakesToDeliver = pancakeService.getPancakeDescriptionsForOrder(orderId);

        OrderLog.logDeliverOrder(order, pancakeService.getPancakes()); // or pass just the relevant ones

        pancakeService.removePancakesForOrder(orderId);
        orderService.removeOrder(orderId);

        return new Object[] {order, pancakesToDeliver};
    }

}