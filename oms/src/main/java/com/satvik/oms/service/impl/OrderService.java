package com.satvik.oms.service.impl;

import com.satvik.oms.entity.Order;
import com.satvik.oms.entity.OrderItem;
import com.satvik.oms.entity.OrderStatus;
import com.satvik.oms.entity.Payment;
import com.satvik.oms.entity.Product;
import com.satvik.oms.entity.User;
import com.satvik.oms.exception.InsufficientBalanceException;
import com.satvik.oms.exception.OrderNotFoundException;
import com.satvik.oms.service.IOrderService;
import com.satvik.oms.service.IPaymentService;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.LongSupplier;

public class OrderService implements IOrderService {

    private final IPaymentService paymentService;
    private final Map<String, Order> orderIdToOrder;
    private final LongSupplier timeSupplier;
    private final ConcurrentHashMap<String, ReentrantLock> productLocks = new ConcurrentHashMap<>();

    public OrderService(IPaymentService paymentService, LongSupplier timeSupplier){
        this.paymentService = paymentService;
        orderIdToOrder = new ConcurrentHashMap<>();
        this.timeSupplier = timeSupplier;
    }

    @Override
    public Order createOrder(User user, List<OrderItem> orderItems) {
        String orderId = UUID.randomUUID().toString();
        List<OrderItem> sorted = orderItems
                .stream()
                .sorted(Comparator.comparing(a -> a.getProduct().getId()))
                .toList();

        List<ReentrantLock> acquired = new ArrayList<>();
        try {
            sorted.forEach(q -> {
                ReentrantLock lock = getLockFor(q.getProduct().getId());
                lock.lock();
                acquired.add(lock);
            });
            sorted.forEach(q -> {
                Product p = q.getProduct();
                q.setOrderId(orderId);
                if (q.getQuantity() > p.getQuantity()) {
                    throw new IllegalArgumentException("Product " + p.getSku() + " is out of stock");
                }
            });
            sorted.forEach(q -> {
                Product p = q.getProduct();
                p.setQuantity(p.getQuantity() - q.getQuantity());
            });
        } finally {
            acquired.forEach(ReentrantLock::unlock);
        }

        Order order = Order.builder()
                .id(orderId)
                .user(user)
                .orderItems(orderItems)
                .status(OrderStatus.CREATED)
                .createdAt(Instant.ofEpochSecond(timeSupplier.getAsLong()))
                .build();
        orderIdToOrder.put(orderId, order);
        return order;
    }

    @Override
    public String checkoutOrder(String orderId) {
        Order order = orderIdToOrder.get(orderId);
        if(order == null){
            throw new OrderNotFoundException("Order " + orderId + " not found");
        }
        synchronized (order) {
            if (order.getStatus() == OrderStatus.CONFIRMED || order.getStatus() == OrderStatus.CANCELLED) {
                throw new IllegalStateException("Order " + orderId + " is already " + order.getStatus());
            }
            try {
                Payment payment = paymentService.debit(order);
                order.setStatus(OrderStatus.CONFIRMED);
                return payment.getId();
            } catch (InsufficientBalanceException ex) {
                cancelOrder(orderId);
                throw ex;
            }
        }
    }

    @Override
    public String cancelOrder(String orderId) {
        Order order = orderIdToOrder.get(orderId);
        if(order == null){
            throw new OrderNotFoundException("Order " + orderId + " not found");
        }
        synchronized (order) {
            if (order.getStatus() == OrderStatus.CONFIRMED || order.getStatus() == OrderStatus.CANCELLED) {
                throw new IllegalStateException("Order " + orderId + " is already " + order.getStatus());
            }

            order.getOrderItems().forEach(q -> {
                Product p = q.getProduct();
                ReentrantLock lock = getLockFor(p.getId());
                lock.lock();
                try {
                    p.setQuantity(p.getQuantity() + q.getQuantity());
                } finally {
                    lock.unlock();
                }
            });
            order.setStatus(OrderStatus.CANCELLED);
        }
        return orderId;
    }

    private ReentrantLock getLockFor(String id) {
        return productLocks.computeIfAbsent(id, k -> new ReentrantLock());
    }
}
