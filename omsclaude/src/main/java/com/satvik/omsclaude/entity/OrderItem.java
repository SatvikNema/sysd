package com.satvik.omsclaude.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderItem {
    private final Product product;
    private final int quantity;
}
