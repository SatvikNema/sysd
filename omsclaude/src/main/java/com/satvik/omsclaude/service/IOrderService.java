package com.satvik.omsclaude.service;

import com.satvik.omsclaude.entity.Order;
import com.satvik.omsclaude.entity.OrderItem;
import com.satvik.omsclaude.entity.User;

import java.util.List;

public interface IOrderService {
    Order createOrder(User user, List<OrderItem> items);
    Order checkout(String orderId);
    Order cancel(String orderId);
}
