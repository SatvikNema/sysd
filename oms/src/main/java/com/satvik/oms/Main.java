package com.satvik.oms;

import com.satvik.oms.entity.Order;
import com.satvik.oms.entity.OrderItem;
import com.satvik.oms.entity.Product;
import com.satvik.oms.entity.User;
import com.satvik.oms.exception.InsufficientBalanceException;
import com.satvik.oms.service.IOrderService;
import com.satvik.oms.service.IPaymentService;
import com.satvik.oms.service.impl.OrderService;
import com.satvik.oms.service.impl.PaymentService;

import java.util.List;
import java.util.UUID;
import java.util.function.LongSupplier;

public class Main {
    public static void main(String[] args) {
        List<Product> products = List.of(
                Product.builder().id(UUID.randomUUID().toString()).sku("Pegasus 41").price(10).quantity(3).build(),
                Product.builder().id(UUID.randomUUID().toString()).sku("Head Lamp").price(20).quantity(2).build(),
                Product.builder().id(UUID.randomUUID().toString()).sku("Garmin Instinct Solar 2X").price(30).quantity(4).build()
        );

        List<User> users = List.of(
                User.builder().id(UUID.randomUUID().toString()).name("Satvik").balance(70).build(),
                User.builder().id(UUID.randomUUID().toString()).name("Keshav").balance(60).build(),
                User.builder().id(UUID.randomUUID().toString()).name("Kartik").balance(50).build()
        );

        long[] timeArr = new long[]{17_000_000_000L};
        LongSupplier longSupplier = () -> timeArr[0];
        IPaymentService paymentService = new PaymentService();
        IOrderService orderService = new OrderService(paymentService, longSupplier);

        List<OrderItem> satvikOrderItems = List.of(
                OrderItem.builder().product(products.get(0)).quantity(2).build(),
                OrderItem.builder().product(products.get(1)).quantity(1).build(),
                OrderItem.builder().product(products.get(2)).quantity(1).build()
        );

        Order order = orderService.createOrder(users.get(0), satvikOrderItems);
        printRemainingInventory(products);
        printRemainingBalance(users);

        orderService.checkoutOrder(order.getId());
        printRemainingInventory(products);
        printRemainingBalance(users);

        satvikOrderItems = List.of(
                OrderItem.builder().product(products.get(0)).quantity(1).build(),
                OrderItem.builder().product(products.get(1)).quantity(1).build(),
                OrderItem.builder().product(products.get(2)).quantity(1).build()
        );
        order = orderService.createOrder(users.get(0), satvikOrderItems);
        try {
            orderService.checkoutOrder(order.getId());
        } catch(InsufficientBalanceException ex){
            System.out.println(ex.getMessage());
        }
        printRemainingInventory(products);
        printRemainingBalance(users);
    }

    private static void printRemainingInventory(List<Product> products) {
        products.forEach(p -> {
            System.out.println(p.getSku() + " : " + p.getQuantity() + " left");
        });
        System.out.println("==");
    }

    private static void printRemainingBalance(List<User> users) {
        users.forEach(u -> {
            System.out.println(u.getName() + " : " + u.getBalance() + " remaining");
        });
        System.out.println("==");
    }

}

/**
 entities:
    user - id, name
    product - id, sku, quantity
    order_item - id, orderId, productId, quantity
    order - id, list(orderItem), userId, status
    payment - id, idempotencyKey, orderId, status

 services
    paymentService -
                   debit(Order)
                   revert(paymentId)
    orderService -
                   createOrder(list(orderItem), userId)
                   checkoutOrder(order)
                   cancelOrder(order)

 */
