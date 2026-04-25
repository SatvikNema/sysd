package com.satvik.oms.service;

import com.satvik.oms.entity.Order;
import com.satvik.oms.entity.OrderItem;
import com.satvik.oms.entity.User;

import java.util.List;

public interface IOrderService {
    Order createOrder(User user, List<OrderItem> orderItems);
    String checkoutOrder(String orderId);
    String cancelOrder(String orderId);
}
