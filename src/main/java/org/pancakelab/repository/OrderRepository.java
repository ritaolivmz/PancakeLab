package org.pancakelab.repository;

import org.pancakelab.model.Order;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class OrderRepository {
    private final Map<UUID, Order> orders = new ConcurrentHashMap<>();
    private final Set<UUID> completedOrders = ConcurrentHashMap.newKeySet();
    private final Set<UUID> preparedOrders = ConcurrentHashMap.newKeySet();

    public void save(Order o) { orders.put(o.getId(), o); }
    public Optional<Order> findById(UUID id) { return Optional.ofNullable(orders.get(id)); }
    public void delete(UUID id) { orders.remove(id); preparedOrders.remove(id); completedOrders.remove(id); }
    public Collection<Order> findAll() { return orders.values(); }
    public void markCompleted(UUID id) { completedOrders.add(id); }
    public void markPrepared(UUID id) { preparedOrders.add(id); completedOrders.remove(id); }
    public boolean isPrepared(UUID id) { return preparedOrders.contains(id); }

    public Set<UUID> getCompletedOrders() {
        return completedOrders;
    }

    public Set<UUID> getPreparedOrders() {
        return preparedOrders;
    }

    public Map<UUID, Order> getOrders() {
        return orders;
    }
}
