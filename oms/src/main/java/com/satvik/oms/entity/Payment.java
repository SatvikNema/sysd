package com.satvik.oms.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Payment {
    private String id;
    private String idempotencyKey;
    private String orderId;
    private double amount;
    private PaymentStatus status;
}
