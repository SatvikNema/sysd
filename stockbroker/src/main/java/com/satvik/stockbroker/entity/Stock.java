package com.satvik.stockbroker.entity;

import com.satvik.stockbroker.model.OrderQueue;
import lombok.Builder;
import lombok.Data;

import java.util.concurrent.atomic.AtomicLong;

@Data
@Builder
public class Stock {
    private String id;
    private String name;
    private AtomicLong currentPrice;
    private OrderQueue orderQueue;
}
