package com.satvik.stockbroker.service;

import com.satvik.stockbroker.entity.Order;
import com.satvik.stockbroker.entity.OrderType;

import java.util.List;
import java.util.Optional;

public interface IOrderService {
     List<Order> getOrders(String userId);

     Order placeOrder(String userId,
                      OrderType orderType,
                      String stockId,
                      int quantity,
                      long price);

     void matchOrder(String orderId);

     Optional<Order> getOrder(String orderId);

     List<Order> getAllOrders();
}
