package com.satvik.stockbroker.entity;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;

@Data
@Builder
@EqualsAndHashCode(of = "id")
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

    @Override
    public String toString() {
        return "Order{" +
                "id='" + id + '\'' +
                ", user=" + user.getName() +
                ", stock=" + stock.getName() +
                ", quantity=" + quantity +
                ", quantityFilled=" + quantityFilled +
                ", price=" + price +
                ", createdAt=" + createdAt +
                ", orderStatus=" + orderStatus +
                ", orderType=" + orderType +
                '}';
    }
}
