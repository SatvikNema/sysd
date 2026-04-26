package com.satvik.omsclaude.repo;

import com.satvik.omsclaude.entity.Payment;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PaymentRepository {
    private final Map<String, Payment> byId = new ConcurrentHashMap<>();
    private final Map<String, Payment> byIdempotencyKey = new ConcurrentHashMap<>();

    /**
     * Atomically registers a payment by idempotency key. Returns the winning Payment —
     * either the exact instance passed in (caller won the race) or a pre-existing one
     * (caller lost). Stores the reference as-is; callers may rely on `==` to detect a win.
     */
    public Payment registerIfAbsent(Payment payment) {
        Payment existing = byIdempotencyKey.putIfAbsent(payment.getIdempotencyKey(), payment);
        if (existing != null) return existing;
        byId.put(payment.getId(), payment);
        return payment;
    }
}
