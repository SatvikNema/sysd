package com.satvik.stockbroker.model;

import com.satvik.stockbroker.entity.Order;
import com.satvik.stockbroker.entity.OrderType;
import lombok.Getter;

import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;

@Getter
public class OrderQueue {
    private final TreeMap<Long, List<Order>> sellOrders;
    private final TreeMap<Long, List<Order>> buyOrders;
    public OrderQueue(){
        this.sellOrders = new TreeMap<>();
        this.buyOrders = new TreeMap<>(Comparator.reverseOrder());
    }

    public void removeTuple(Order sellOrder, Order buyOrder){
        long sellPrice = sellOrder.getPrice();
        sellOrders.get(sellPrice).remove(sellOrder);
        cleanup(sellOrders, sellPrice);

        long buyPrice = buyOrder.getPrice();
        buyOrders.get(buyPrice).remove(buyOrder);
        cleanup(buyOrders, buyPrice);
    }

    public void remove(OrderType orderType, Order order) {
        long sellPrice = 0;
        if (orderType == OrderType.BUY) {
            long buyPrice = order.getPrice();
            buyOrders.get(buyPrice).remove(order);
            cleanup(buyOrders, buyPrice);
        } else if (orderType == OrderType.SELL) {
            sellPrice = order.getPrice();
            sellOrders.get(sellPrice).remove(order);
            cleanup(sellOrders, sellPrice);
        }
    }

    private void cleanup(TreeMap<Long, List<Order>> orders, Long key){
        if(orders.containsKey(key) && orders.get(key).isEmpty()){
            orders.remove(key);
        }
    }
}
