package org.pancakelab.service;

import org.pancakelab.model.Order;
import org.pancakelab.model.OrderStatus;
import org.pancakelab.repository.OrderRepository;
import org.pancakelab.validators.BuildingValidator;
import org.pancakelab.validators.InputValidator;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class OrderService {

    private final OrderRepository orderRepo;

    OrderService(OrderRepository orderRepo) {
        this.orderRepo = orderRepo;
    }

    Set<UUID> listCompletedOrders() {
        return orderRepo.getCompletedOrders();
    }

    Set<UUID> listPreparedOrders() {
        return orderRepo.getPreparedOrders();
    }

    Map<UUID, Order> getOrders() {
        return orderRepo.getOrders();
    }

    Order createOrder(int building, int room) {
        BuildingValidator.validateBuildingExists(building);
        BuildingValidator.validateRoomExists(building, room);
        Order order = new Order(building, room);
        orderRepo.save(order);
        return order;
    }

    void completeOrder(UUID orderId) {
        InputValidator.validateOrderId(orderId);
        Optional<Order> orderOptional = orderRepo.findAll().stream()
                .filter(o -> o.getId().equals(orderId))
                .findFirst();

        Order order = orderOptional.orElseThrow(() ->
                new IllegalArgumentException("The provided order ID " + orderId + " was not found.")
        );

        if (order.getStatus() == OrderStatus.COMPLETED) {
            throw new IllegalStateException("The provided order ID " + orderId + " is already completed.");
        }

        orderRepo.markCompleted(order);
    }

    void prepareOrder(UUID orderId) {
        InputValidator.validateOrderId(orderId);
        Optional<Order> orderOptional = orderRepo.findAll().stream()
                .filter(o -> o.getId().equals(orderId))
                .findFirst();
        Order order = orderOptional.orElseThrow(() ->
                new IllegalArgumentException("The provided order ID " + orderId + " was not found.")
        );

        if (order.getStatus() == OrderStatus.PREPARED) {
            throw new IllegalStateException("Order " + orderId + " is already prepared.");
        }

        orderRepo.markPrepared(orderId);
    }

    boolean isPrepared(UUID orderId) {
        InputValidator.validateOrderId(orderId);
        return orderRepo.isPrepared(orderId);
    }

    Optional<Order> getOrder(UUID orderId) {
        InputValidator.validateOrderId(orderId);
        return Optional.ofNullable(orderRepo.findById(orderId));
    }

    void removeOrder(UUID orderId) {
        InputValidator.validateOrderId(orderId);
        orderRepo.delete(orderId);
    }
}