package org.pancakelab.model;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class Order {
    private static final AtomicInteger idGenerator = new AtomicInteger();
    private final UUID id;
    private final int building;
    private final int room;

    public Order(int building, int room) {
        if (building <= 0 || room <= 0) throw new IllegalArgumentException("Invalid room or building.");
        this.id = UUID.randomUUID();
        this.building = building;
        this.room = room;
    }

    public UUID getId() { return id; }
    public int getBuilding() { return building; }
    public int getRoom() { return room; }
}
