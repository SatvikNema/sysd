package com.satvik.stockbroker.entity;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class Trade {
    private String id;
    private String sellId;
    private String buyId;
    private long price;
    private int quantity;
    private Instant executedAt;

    @Override
    public String toString() {
        return "Trade{" +
                "sellId='" + sellId + '\'' +
                ", buyId='" + buyId + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", executedAt=" + executedAt +
                '}';
    }
}
