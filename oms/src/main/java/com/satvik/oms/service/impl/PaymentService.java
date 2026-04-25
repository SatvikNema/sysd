package com.satvik.oms.service.impl;

import com.satvik.oms.entity.Order;
import com.satvik.oms.entity.Payment;
import com.satvik.oms.entity.PaymentStatus;
import com.satvik.oms.entity.User;
import com.satvik.oms.exception.InsufficientBalanceException;
import com.satvik.oms.exception.PaymentAlreadyInProgressException;
import com.satvik.oms.service.IPaymentService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PaymentService implements IPaymentService {
    private final Map<String, Payment> paymentIdToPayment;
    private final Map<String, Payment> ikToPayment;

    public PaymentService(){
        this.paymentIdToPayment = new ConcurrentHashMap<>();
        this.ikToPayment = new ConcurrentHashMap<>();
    }
    @Override
    public Payment debit(Order order) {
        String id = UUID.randomUUID().toString();
        String idempotencyKey = order.getId();

        Payment payment = Payment.builder()
                .id(id)
                .orderId(order.getId())
                .amount(order.getOrderItems()
                        .stream()
                        .mapToDouble(q -> q.getProduct().getPrice() * q.getQuantity())
                        .sum())
                .status(PaymentStatus.PENDING)
                .idempotencyKey(idempotencyKey)
                .build();
        Payment existingPayment = ikToPayment.putIfAbsent(idempotencyKey, payment);

        if(existingPayment != null){
            throw new
                PaymentAlreadyInProgressException("Payment for order " + order.getId() + " is already registered in the system");
        }
        paymentIdToPayment.put(id, payment);
        User user = order.getUser();
        synchronized(user) {
            double balance = user.getBalance();
            if (payment.getAmount() <= balance) {
                user.setBalance(balance - payment.getAmount());
                payment.setStatus(PaymentStatus.SUCCESS);
            } else {
                payment.setStatus(PaymentStatus.FAILED);
                throw new InsufficientBalanceException("User " + user.getName() + " has insufficient balance to complete the payment: " + order.getUser().getBalance() + ". Required " + payment.getAmount());
            }
        }
        return payment;
    }
}
