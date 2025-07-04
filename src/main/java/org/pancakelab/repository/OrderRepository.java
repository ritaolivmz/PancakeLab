package org.pancakelab.repository;

import org.pancakelab.model.Order;
import org.pancakelab.model.OrderStatus;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class OrderRepository {

    private final Map<UUID, Order> orders = new ConcurrentHashMap<>();
    private final Object lock = new Object();

    public void save(Order o) {
        orders.put(o.getId(), o);
    }

    public void delete(UUID id) {
        orders.remove(id);
    }

    public Order findById(UUID id) {
        return orders.get(id);
    }

    public Collection<Order> findAll() {
        return new ArrayList<>(orders.values());
    }

    public void markCompleted(UUID id) {
        synchronized (lock) {
            Order order = orders.get(id);
            if (order != null) {
                order.setStatus(OrderStatus.COMPLETED);
            }
        }
    }

    public void markCompleted(Order order) {
        synchronized (lock) {
            if (order != null) {
                order.setStatus(OrderStatus.COMPLETED);
            }
        }
    }

    public void markPrepared(UUID id) {
        synchronized (lock) {
            Order order = orders.get(id);
            if (order != null) {
                order.setStatus(OrderStatus.PREPARED);
            }
        }
    }

    public boolean isPrepared(UUID id) {
        Order order = orders.get(id);
        return order != null && order.getStatus() == OrderStatus.PREPARED;
    }

    public Set<UUID> getCompletedOrders() {
        return orders.values().stream()
                .filter(order -> order.getStatus() == OrderStatus.COMPLETED)
                .map(Order::getId)
                .collect(Collectors.toSet());
    }

    public Set<UUID> getPreparedOrders() {
        return orders.values().stream()
                .filter(order -> order.getStatus() == OrderStatus.PREPARED)
                .map(Order::getId)
                .collect(Collectors.toSet());
    }

    public Map<UUID, Order> getOrders() {
        return new ConcurrentHashMap<>(orders);
    }
}