package com.satvik.omsclaude.service.impl;

import com.satvik.omsclaude.entity.Order;
import com.satvik.omsclaude.entity.OrderItem;
import com.satvik.omsclaude.entity.OrderStatus;
import com.satvik.omsclaude.entity.Product;
import com.satvik.omsclaude.entity.User;
import com.satvik.omsclaude.exception.InsufficientBalanceException;
import com.satvik.omsclaude.exception.InsufficientStockException;
import com.satvik.omsclaude.exception.InvalidOrderStateException;
import com.satvik.omsclaude.exception.OrderNotFoundException;
import com.satvik.omsclaude.repo.OrderRepository;
import com.satvik.omsclaude.service.IOrderService;
import com.satvik.omsclaude.service.IPaymentService;

import java.time.Clock;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class OrderService implements IOrderService {

    private final IPaymentService paymentService;
    private final OrderRepository orderRepository;
    private final ScheduledExecutorService scheduler;
    private final Clock clock;
    private final Duration checkoutTtl;

    public OrderService(IPaymentService paymentService,
                        OrderRepository orderRepository,
                        ScheduledExecutorService scheduler,
                        Clock clock,
                        Duration checkoutTtl) {
        this.paymentService = paymentService;
        this.orderRepository = orderRepository;
        this.scheduler = scheduler;
        this.clock = clock;
        this.checkoutTtl = checkoutTtl;
    }

    @Override
    public Order createOrder(User user, List<OrderItem> items) {
        // Reserve inventory all-or-nothing. Track what we got so we can roll back.
        List<OrderItem> reserved = new ArrayList<>(items.size());
        for (OrderItem item : items) {
            Product p = item.getProduct();
            if (!p.tryReserve(item.getQuantity())) {
                reserved.forEach(r -> r.getProduct().release(r.getQuantity()));
                throw new InsufficientStockException(
                        "Product " + p.getSku() + " out of stock (have " + p.getQuantity()
                                + ", need " + item.getQuantity() + ")");
            }
            reserved.add(item);
        }

        Order order = new Order(
                UUID.randomUUID().toString(),
                user,
                items,
                clock.instant());
        orderRepository.save(order);

        scheduler.schedule(() -> autoCancel(order.getId()),
                checkoutTtl.toMillis(), TimeUnit.MILLISECONDS);

        return order;
    }

    @Override
    public Order checkout(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order " + orderId + " not found"));

        // CAS: only one thread (and not the timer) wins this transition.
        if (!order.transition(OrderStatus.CREATED, OrderStatus.PAYING)) {
            throw new InvalidOrderStateException(
                    "Order " + orderId + " is " + order.getStatusValue() + ", cannot checkout");
        }

        try {
            paymentService.debit(order);
            order.transition(OrderStatus.PAYING, OrderStatus.CONFIRMED);
            return order;
        } catch (InsufficientBalanceException ex) {
            // Roll back: release stock, mark cancelled.
            releaseInventory(order);
            order.transition(OrderStatus.PAYING, OrderStatus.CANCELLED);
            throw ex;
        }
    }

    @Override
    public Order cancel(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order " + orderId + " not found"));

        if (!order.transition(OrderStatus.CREATED, OrderStatus.CANCELLED)) {
            throw new InvalidOrderStateException(
                    "Order " + orderId + " is " + order.getStatusValue() + ", cannot cancel");
        }
        releaseInventory(order);
        return order;
    }

    private void autoCancel(String orderId) {
        orderRepository.findById(orderId).ifPresent(order -> {
            // Only act if still in CREATED. If user already moved to PAYING/CONFIRMED, no-op.
            if (order.transition(OrderStatus.CREATED, OrderStatus.CANCELLED)) {
                releaseInventory(order);
            }
        });
    }

    private void releaseInventory(Order order) {
        order.getOrderItems().forEach(i -> i.getProduct().release(i.getQuantity()));
    }
}
