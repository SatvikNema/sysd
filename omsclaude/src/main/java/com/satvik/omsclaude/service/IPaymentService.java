package com.satvik.omsclaude.service;

import com.satvik.omsclaude.entity.Order;
import com.satvik.omsclaude.entity.Payment;

public interface IPaymentService {
    /**
     * Idempotent: calling debit twice for the same order returns the original payment
     * and never debits the user twice.
     */
    Payment debit(Order order);
}
