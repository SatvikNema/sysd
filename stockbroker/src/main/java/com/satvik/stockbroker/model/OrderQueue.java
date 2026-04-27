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

        long buyPrice = buyOrder.getPrice();
        buyOrders.get(buyPrice).remove(buyOrder);
    }

    public void remove(OrderType orderType, Order order){
        if(orderType == OrderType.BUY){
            long buyPrice = order.getPrice();
            buyOrders.get(buyPrice).remove(order);
        }else if(orderType == OrderType.SELL){
            long sellPrice = order.getPrice();
            sellOrders.get(sellPrice).remove(order);
        }
    }
}
