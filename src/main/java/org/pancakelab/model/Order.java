package org.pancakelab.model;

import org.pancakelab.validators.InputValidator;

import java.util.Objects;
import java.util.UUID;

public class Order {

    private final UUID id;
    private final int building;
    private final int room;
    private OrderStatus status;

    public Order(int building, int room) {
        InputValidator.validateNonNegativeNumber(building, room);
        this.id = UUID.randomUUID();
        this.building = building;
        this.room = room;
        this.status = OrderStatus.CREATED;
    }

    public UUID getId() { return id; }
    public int getBuilding() { return building; }
    public int getRoom() { return room; }
    public OrderStatus getStatus() { return status; }

    public void setStatus(OrderStatus status) { this.status = status; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
