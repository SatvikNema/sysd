package com.satvik.oms.entity;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class Order {
    private String id;
    private List<OrderItem> orderItems;
    private OrderStatus status;
    private User user;
    private Instant createdAt;
}
