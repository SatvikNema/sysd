package com.satvik.stockbroker.entity;

import com.satvik.stockbroker.model.OrderQueue;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.concurrent.atomic.AtomicLong;

@Data
@Builder
@EqualsAndHashCode(of = "id")
public class Stock {
    private String id;
    private String name;
    private AtomicLong currentPrice;
    private OrderQueue orderQueue;
}
