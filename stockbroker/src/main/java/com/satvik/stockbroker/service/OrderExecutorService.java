package com.satvik.stockbroker.service;

import com.satvik.stockbroker.entity.OrderStatus;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class OrderExecutorService {
    private final IOrderService orderService;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public OrderExecutorService(IOrderService orderService) {
        this.orderService = orderService;
    }

    public void start(){
        executor.execute(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    orderService.getAllOrders().stream()
                            .filter(order -> order.getOrderStatus() == OrderStatus.PENDING || order.getOrderStatus() == OrderStatus.PARTIALLY_FULFILLED)
                            .forEach(order -> orderService.matchOrder(order.getId()));
                    System.out.println("-");
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
    }

    public void shutDown() throws InterruptedException {
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
    }
}
