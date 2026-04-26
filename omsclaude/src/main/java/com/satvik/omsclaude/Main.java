package com.satvik.omsclaude;

import com.satvik.omsclaude.entity.Order;
import com.satvik.omsclaude.entity.OrderItem;
import com.satvik.omsclaude.entity.OrderStatus;
import com.satvik.omsclaude.entity.Product;
import com.satvik.omsclaude.entity.User;
import com.satvik.omsclaude.exception.InsufficientBalanceException;
import com.satvik.omsclaude.exception.InsufficientStockException;
import com.satvik.omsclaude.exception.InvalidOrderStateException;
import com.satvik.omsclaude.repo.OrderRepository;
import com.satvik.omsclaude.repo.PaymentRepository;
import com.satvik.omsclaude.service.IOrderService;
import com.satvik.omsclaude.service.IPaymentService;
import com.satvik.omsclaude.service.impl.OrderService;
import com.satvik.omsclaude.service.impl.PaymentService;

import java.time.Clock;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    public static void main(String[] args) throws Exception {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
        try {
            scenarioHappyPath(scheduler);
            scenarioConcurrentCheckoutSameOrder(scheduler);
            scenarioRaceForLastUnit(scheduler);
            scenarioAutoCancel(scheduler);
            scenarioInsufficientBalance(scheduler);
        } finally {
            scheduler.shutdownNow();
        }
    }

    // ---------- scenario 1: happy path ----------
    private static void scenarioHappyPath(ScheduledExecutorService scheduler) {
        banner("happy path");
        Product shoe = product("Pegasus 41", 10, 3);
        User u = user("Satvik", 100);

        IOrderService oms = newOms(scheduler, Duration.ofMinutes(5));
        Order o = oms.createOrder(u, List.of(item(shoe, 2)));
        oms.checkout(o.getId());

        System.out.println("status=" + o.getStatusValue() + " stock=" + shoe.getQuantity()
                + " balance=" + u.getBalance());
    }

    // ---------- scenario 2: 10 threads checking out same order ----------
    private static void scenarioConcurrentCheckoutSameOrder(ScheduledExecutorService scheduler)
            throws InterruptedException {
        banner("concurrent checkout of same order — exactly one wins");
        Product shoe = product("Pegasus 41", 10, 5);
        User u = user("Satvik", 1000);
        IOrderService oms = newOms(scheduler, Duration.ofMinutes(5));
        Order o = oms.createOrder(u, List.of(item(shoe, 2)));

        AtomicInteger ok = new AtomicInteger();
        AtomicInteger rejected = new AtomicInteger();
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(10);
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                try {
                    start.await();
                    oms.checkout(o.getId());
                    ok.incrementAndGet();
                } catch (InvalidOrderStateException ex) {
                    rejected.incrementAndGet();
                } catch (Exception ignored) {
                } finally {
                    done.countDown();
                }
            }).start();
        }
        start.countDown();
        done.await();
        System.out.println("checkouts ok=" + ok + " rejected=" + rejected
                + " balance=" + u.getBalance() + " (debited once: " + (u.getBalance() == 980.0) + ")");
    }

    // ---------- scenario 3: many threads, last unit of stock ----------
    private static void scenarioRaceForLastUnit(ScheduledExecutorService scheduler)
            throws InterruptedException {
        banner("100 threads racing for 5 units of stock");
        Product shoe = product("Pegasus 41", 10, 5);
        IOrderService oms = newOms(scheduler, Duration.ofMinutes(5));

        AtomicInteger placed = new AtomicInteger();
        AtomicInteger oos = new AtomicInteger();
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(100);
        for (int i = 0; i < 100; i++) {
            User u = user("user-" + i, 1000);
            new Thread(() -> {
                try {
                    start.await();
                    oms.createOrder(u, List.of(item(shoe, 1)));
                    placed.incrementAndGet();
                } catch (InsufficientStockException e) {
                    oos.incrementAndGet();
                } catch (Exception ignored) {
                } finally {
                    done.countDown();
                }
            }).start();
        }
        start.countDown();
        done.await();
        System.out.println("placed=" + placed + " out-of-stock=" + oos
                + " stock=" + shoe.getQuantity() + " (invariant placed==5: " + (placed.get() == 5) + ")");
    }

    // ---------- scenario 4: auto-cancel reverses inventory ----------
    private static void scenarioAutoCancel(ScheduledExecutorService scheduler)
            throws InterruptedException {
        banner("auto-cancel after TTL releases inventory");
        Product shoe = product("Pegasus 41", 10, 3);
        User u = user("Satvik", 100);
        IOrderService oms = newOms(scheduler, Duration.ofMillis(200));

        Order o = oms.createOrder(u, List.of(item(shoe, 2)));
        System.out.println("after create: stock=" + shoe.getQuantity() + " status=" + o.getStatusValue());
        Thread.sleep(400);
        System.out.println("after ttl:    stock=" + shoe.getQuantity() + " status=" + o.getStatusValue());
        try {
            oms.checkout(o.getId());
        } catch (InvalidOrderStateException ex) {
            System.out.println("checkout rejected as expected: " + ex.getMessage());
        }
    }

    // ---------- scenario 5: insufficient balance rolls back stock ----------
    private static void scenarioInsufficientBalance(ScheduledExecutorService scheduler) {
        banner("insufficient balance: stock is restored");
        Product shoe = product("Pegasus 41", 10, 3);
        User broke = user("Broke", 5);
        IOrderService oms = newOms(scheduler, Duration.ofMinutes(5));

        Order o = oms.createOrder(broke, List.of(item(shoe, 2)));
        System.out.println("after create: stock=" + shoe.getQuantity());
        try {
            oms.checkout(o.getId());
        } catch (InsufficientBalanceException ex) {
            System.out.println("checkout failed: " + ex.getMessage());
        }
        System.out.println("after fail:   stock=" + shoe.getQuantity() + " status=" + o.getStatusValue());
    }

    // ---------- factories ----------
    private static IOrderService newOms(ScheduledExecutorService scheduler, Duration ttl) {
        PaymentRepository payRepo = new PaymentRepository();
        OrderRepository orderRepo = new OrderRepository();
        IPaymentService payments = new PaymentService(payRepo);
        return new OrderService(payments, orderRepo, scheduler, Clock.systemUTC(), ttl);
    }

    private static Product product(String sku, double price, int qty) {
        return new Product(UUID.randomUUID().toString(), sku, price, qty);
    }

    private static User user(String name, double balance) {
        return User.builder().id(UUID.randomUUID().toString()).name(name).balance(balance).build();
    }

    private static OrderItem item(Product p, int q) {
        return OrderItem.builder().product(p).quantity(q).build();
    }

    private static void banner(String s) {
        System.out.println("\n=== " + s + " ===");
    }
}
