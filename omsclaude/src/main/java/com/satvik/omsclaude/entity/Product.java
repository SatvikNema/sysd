package com.satvik.omsclaude.entity;

import lombok.Getter;

import java.util.concurrent.atomic.AtomicInteger;

@Getter
public class Product {
    private final String id;
    private final String sku;
    private final double price;
    private final AtomicInteger available;

    public Product(String id, String sku, double price, int initialQuantity) {
        this.id = id;
        this.sku = sku;
        this.price = price;
        this.available = new AtomicInteger(initialQuantity);
    }

    /** CAS-loop reserve. Returns true if reserved, false if insufficient stock. */
    public boolean tryReserve(int qty) {
        while (true) {
            int current = available.get();
            if (current < qty) return false;
            if (available.compareAndSet(current, current - qty)) return true;
        }
    }

    public void release(int qty) {
        available.addAndGet(qty);
    }

    public int getQuantity() {
        return available.get();
    }
}
