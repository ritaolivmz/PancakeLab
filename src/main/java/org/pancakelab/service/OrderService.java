package org.pancakelab.service;

import org.pancakelab.model.Order;
import org.pancakelab.repository.OrderRepository;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class OrderService {

    private final OrderRepository orderRepo;

    public OrderService(OrderRepository orderRepo) {
        this.orderRepo = orderRepo;
    }

    public Order createOrder(int building, int room) {
        validateBuildingAndRoom(building, room);
        Order order = new Order(building, room);
        orderRepo.save(order);
        return order;
    }

    public void completeOrder(UUID orderId) {
        orderRepo.markCompleted(orderId);
    }

    public Set<UUID> listCompletedOrders() {
        return orderRepo.getCompletedOrders();
    }

    public Set<UUID> listPreparedOrders() {
        return orderRepo.getPreparedOrders();
    }

    public Map<UUID, Order> getOrders() {
        return orderRepo.getOrders();
    }

    public void prepareOrder(UUID orderId) {
        orderRepo.markPrepared(orderId);
    }

    public boolean isPrepared(UUID orderId) {
        return orderRepo.isPrepared(orderId);
    }

    private void validateBuildingAndRoom(int building, int room) {
        if (building <= 0 || room <= 0) {
            throw new IllegalArgumentException("Invalid building or room number.");
        }
    }

    public Optional<Order> getOrder(UUID orderId) {
        return orderRepo.findAll().stream()
                .filter(o -> o.getId().equals(orderId))
                .findFirst();
    }

    public void removeOrder(UUID orderId) {
        orderRepo.delete(orderId);
    }

}
