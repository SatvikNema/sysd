package com.satvik.omsclaude.repo;

import com.satvik.omsclaude.entity.Order;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class OrderRepository {
    private final Map<String, Order> store = new ConcurrentHashMap<>();

    public void save(Order order) {
        store.put(order.getId(), order);
    }

    public Optional<Order> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }
}
