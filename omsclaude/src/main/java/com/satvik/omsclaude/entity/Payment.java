package com.satvik.omsclaude.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Payment {
    private final String id;
    private final String idempotencyKey;
    private final String orderId;
    private final double amount;
    private PaymentStatus status;
}
