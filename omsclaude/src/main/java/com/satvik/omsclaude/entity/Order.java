package com.satvik.omsclaude.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Getter
public class Order {
    private final String id;
    private final User user;
    private final List<OrderItem> orderItems;
    private final Instant createdAt;
    private final AtomicReference<OrderStatus> status;

    public Order(String id, User user, List<OrderItem> orderItems, Instant createdAt) {
        this.id = id;
        this.user = user;
        this.orderItems = List.copyOf(orderItems);
        this.createdAt = createdAt;
        this.status = new AtomicReference<>(OrderStatus.CREATED);
    }

    public OrderStatus getStatusValue() {
        return status.get();
    }

    public boolean transition(OrderStatus from, OrderStatus to) {
        return status.compareAndSet(from, to);
    }

    public double totalAmount() {
        return orderItems.stream()
                .mapToDouble(i -> i.getProduct().getPrice() * i.getQuantity())
                .sum();
    }
}
