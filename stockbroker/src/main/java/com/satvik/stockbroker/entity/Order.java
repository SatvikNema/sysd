package com.satvik.stockbroker.entity;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class Order {
    private String id;
    private User user;
    private Stock stock;
    private int quantity;
    private int quantityFilled;
    private long price;
    private Instant createdAt;
    private OrderStatus orderStatus;
    private OrderType orderType;
}
