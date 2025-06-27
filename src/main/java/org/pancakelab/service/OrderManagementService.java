package org.pancakelab.service;

import org.pancakelab.model.Order;
import org.pancakelab.model.pancakes.PancakeRecipe;
import org.pancakelab.repository.OrderRepository;
import org.pancakelab.repository.PancakeRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class OrderManagementService {

    private final PancakeService pancakeService;
    private final OrderService orderService;
    private final PancakeOrderService pancakeOrderService;

    public OrderManagementService() {
        OrderRepository orderRepository = new OrderRepository();
        PancakeRepository pancakeRepository = new PancakeRepository();

        this.orderService = new OrderService(orderRepository);
        this.pancakeService = new PancakeService(pancakeRepository);

        this.pancakeOrderService = new PancakeOrderService(pancakeService, orderService);
    }

    public void removePancakesFromOrder(UUID orderId, String description, int count) {
        pancakeOrderService.removePancakesFromOrder(description, orderId, count);
    }

    public List<String> viewOrder(UUID orderId) {
        return pancakeOrderService.viewOrder(orderId);
    }

    public void addPancakesToOrder(UUID orderId, List<String> ingredients, int count) {
        pancakeOrderService.addPancakesToOrder(orderId, ingredients, count);
    }

    public Object[] deliverOrder(UUID orderId) {
        return pancakeOrderService.deliverOrder(orderId);
    }

    public Order createOrder(int building, int room) {
        return orderService.createOrder(building, room);
    }

    public void cancelOrder(UUID orderId) {
        pancakeOrderService.cancelOrder(orderId);
    }

    public void completeOrder(UUID orderId) {
        orderService.completeOrder(orderId);
    }

    public Set<UUID> listCompletedOrders() {
        return orderService.listCompletedOrders();
    }

    public Set<UUID> listPreparedOrders() {
        return orderService.listPreparedOrders();
    }

    public void prepareOrder(UUID orderId) {
        orderService.prepareOrder(orderId);
    }

    public boolean isPrepared(UUID orderId) {
        return orderService.isPrepared(orderId);
    }

    public Optional<Order> getOrder(UUID orderId) {
        return orderService.getOrder(orderId);
    }

    public void removeOrder(UUID orderId) {
        orderService.removeOrder(orderId);
    }

    public void addPancake(List<String> ingredients) {
        pancakeService.addPancake(ingredients);
    }

    public void addPancake(PancakeRecipe pancake) {
        pancakeService.addPancake(pancake);
    }

    public List<PancakeRecipe> getPancakes() {
        return pancakeService.getPancakes();
    }

    public List<String> getPancakeDescriptionsForOrder(UUID orderId) {
        return pancakeService.getPancakeDescriptionsForOrder(orderId);
    }

    public void removePancakesForOrder(UUID orderId) {
        pancakeService.removePancakesForOrder(orderId);
    }

    public List<PancakeRecipe> removePancakes(String description, UUID orderId, int count) {
        return pancakeService.removePancakes(description, orderId, count);
    }
}

