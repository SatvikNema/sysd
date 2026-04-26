package com.satvik.omsclaude.service.impl;

import com.satvik.omsclaude.entity.Order;
import com.satvik.omsclaude.entity.Payment;
import com.satvik.omsclaude.entity.PaymentStatus;
import com.satvik.omsclaude.entity.User;
import com.satvik.omsclaude.exception.InsufficientBalanceException;
import com.satvik.omsclaude.repo.PaymentRepository;
import com.satvik.omsclaude.service.IPaymentService;

import java.util.UUID;

public class PaymentService implements IPaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public Payment debit(Order order) {
        Payment fresh = Payment.builder()
                .id(UUID.randomUUID().toString())
                .idempotencyKey(order.getId())
                .orderId(order.getId())
                .amount(order.totalAmount())
                .status(PaymentStatus.PENDING)
                .build();

        Payment registered = paymentRepository.registerIfAbsent(fresh);
        if (registered != fresh) {
            // already attempted — return existing outcome, no second debit
            if (registered.getStatus() == PaymentStatus.FAILED) {
                throw new InsufficientBalanceException(
                        "Payment for order " + order.getId() + " previously failed");
            }
            return registered;
        }

        User user = order.getUser();
        synchronized (user) {
            if (user.getBalance() < fresh.getAmount()) {
                fresh.setStatus(PaymentStatus.FAILED);
                throw new InsufficientBalanceException(
                        "User " + user.getName() + " balance " + user.getBalance()
                                + " < required " + fresh.getAmount());
            }
            user.setBalance(user.getBalance() - fresh.getAmount());
            fresh.setStatus(PaymentStatus.SUCCESS);
        }
        return fresh;
    }
}
