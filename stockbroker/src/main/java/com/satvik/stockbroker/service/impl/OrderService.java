package com.satvik.stockbroker.service.impl;

import com.satvik.stockbroker.entity.Order;
import com.satvik.stockbroker.entity.OrderStatus;
import com.satvik.stockbroker.entity.OrderType;
import com.satvik.stockbroker.entity.Stock;
import com.satvik.stockbroker.entity.User;
import com.satvik.stockbroker.exception.StockNotFoundException;
import com.satvik.stockbroker.exception.UserNotFoundException;
import com.satvik.stockbroker.service.IOrderService;
import com.satvik.stockbroker.service.IStockService;
import com.satvik.stockbroker.service.IUserService;
import com.satvik.stockbroker.service.OrderFullFillmentService;

import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class OrderService implements IOrderService {
    private final Map<String, Order> orderIdToOrder;
    private final List<Order> orders = new ArrayList<>();
    private final IUserService userService;
    private final IStockService stockService;
    private final OrderFullFillmentService orderFullFillmentService;
    private final Clock clock;

    public OrderService(IUserService userService, IStockService stockService, Clock clock){
        this.orderIdToOrder = new ConcurrentHashMap<>();
        this.userService = userService;
        this.stockService = stockService;
        this.clock = clock;
        this.orderFullFillmentService = new OrderFullFillmentService();
    }

    @Override
    public List<Order> getOrders(String userId) {
        User user = userService.getUser(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id " + userId + " not found"));

        return orderIdToOrder
                .values()
                .stream()
                .filter(o -> userId.equals(o.getUser().getId()))
                .toList();
    }

    @Override
    public Order placeOrder(String userId,
                            OrderType orderType,
                            String stockId,
                            int quantity,
                            long price) {
        User user = userService.getUser(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id " + userId + " not found"));

        Stock stock = stockService.getStock(stockId)
                .orElseThrow(() -> new StockNotFoundException("Stock with id " + stockId + " not found"));

        String id = UUID.randomUUID().toString();
        Order order = Order.builder()
                .id(id)
                .user(user)
                .stock(stock)
                .quantity(quantity)
                .quantityFilled(0)
                .price(price)
                .createdAt(clock.instant())
                .orderStatus(OrderStatus.PENDING)
                .orderType(orderType)
                .build();
        if(orderType == OrderType.SELL){
            List<Order> sellOrders = stock.getOrderQueue().getSellOrders().getOrDefault(order.getPrice(), new ArrayList<>());
            sellOrders.add(order);
            stock.getOrderQueue().getSellOrders().put(order.getPrice(), sellOrders);
        } else if (orderType == OrderType.BUY){
            List<Order> buyOrders = stock.getOrderQueue().getBuyOrders().getOrDefault(order.getPrice(), new ArrayList<>());
            buyOrders.add(order);
            stock.getOrderQueue().getBuyOrders().put(order.getPrice(), buyOrders);
        }

        orderIdToOrder.put(id, order);
        orders.add(order);
        return order;
    }

    @Override
    public void executeOrder(String orderId) {
        Order order = getOrder(orderId)
                .orElseThrow(() -> new StockNotFoundException("Order with id " + orderId + " not found"));
        Stock stock = order.getStock();
        if(order.getOrderType() == OrderType.BUY){
            TreeMap<Long, List<Order>> counterSellOrdersMap = stock.getOrderQueue().getSellOrders();
            int quantityFulfilled = 0;
            List<Order> sellOrdersToFullFill = new ArrayList<>();

            for(Map.Entry<Long, List<Order>> entry : counterSellOrdersMap.entrySet()){
                long lowerSellPrice = entry.getKey();
                if(lowerSellPrice > order.getPrice()) break;
                List<Order> lowestPriceSellOrders = entry.getValue();
                for(Order lowestPriceSellOrder : lowestPriceSellOrders){
                    quantityFulfilled += lowestPriceSellOrder.getQuantity();
                    sellOrdersToFullFill.add(lowestPriceSellOrder);
                    if(quantityFulfilled >= order.getQuantity()){
                        break;
                    }
                }
                if(quantityFulfilled >= order.getQuantity()){
                    break;
                }
            }
            if(sellOrdersToFullFill.isEmpty()) return;
            System.out.println("Got sell order to full fill " + order);
            orderFullFillmentService.fullFillOrders(order, sellOrdersToFullFill);
        } else if(order.getOrderType() == OrderType.SELL){
            TreeMap<Long, List<Order>> counterBuyOrdersMap = stock.getOrderQueue().getBuyOrders();
            int quantityFulfilled = 0;
            List<Order> buyOrdersToFullFill = new ArrayList<>();

            for(Map.Entry<Long, List<Order>> entry : counterBuyOrdersMap.entrySet()){
                long highestBuyPrice = entry.getKey();
                if(highestBuyPrice < order.getPrice()) break;
                List<Order> highestPriceBuyOrders = entry.getValue();
                for(Order highestPriceBuyOrder : highestPriceBuyOrders){
                    quantityFulfilled += highestPriceBuyOrder.getQuantity();
                    buyOrdersToFullFill.add(highestPriceBuyOrder);
                    if(quantityFulfilled >= order.getQuantity()){
                        break;
                    }
                }
                if(quantityFulfilled >= order.getQuantity()){
                    break;
                }
            }
            if(buyOrdersToFullFill.isEmpty()) return;
            System.out.println("Got buy order to full fill " + order);
            orderFullFillmentService.fullFillOrders(order, buyOrdersToFullFill);
        }
    }

    @Override
    public Optional<Order> getOrder(String orderId) {
        return Optional.ofNullable(orderIdToOrder.get(orderId));
    }

    @Override
    public List<Order> getAllOrders() {
        return new ArrayList<>(orders);
    }
}
