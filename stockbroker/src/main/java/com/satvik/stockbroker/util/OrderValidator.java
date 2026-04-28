package com.satvik.stockbroker.util;

import com.satvik.stockbroker.entity.Order;
import com.satvik.stockbroker.entity.OrderStatus;
import com.satvik.stockbroker.entity.PortfolioItem;
import com.satvik.stockbroker.entity.Stock;
import com.satvik.stockbroker.entity.User;

import java.util.Optional;

public class OrderValidator {

    public static void validateSellOrder(User sellingUser, Order sellOrder) {
        Stock stock = sellOrder.getStock();

        Optional<PortfolioItem> itemOptional = sellingUser.getPortfolio()
                .stream()
                .filter(e -> e.getStock().getId().equals(stock.getId()))
                .findFirst();
        if(itemOptional.isEmpty()){
            sellOrder.setOrderStatus(OrderStatus.CANCELLED);
            throw new IllegalStateException("User "+sellingUser.getName()+" does not have "+stock);
        }

        PortfolioItem item = itemOptional.get();
        if(item.getQty() < sellOrder.getQuantity()){
            sellOrder.setOrderStatus(OrderStatus.CANCELLED);
            throw new IllegalStateException("User "+sellingUser.getName()+" does not have enough "+stock);
        }
    }

    public static void validateBuyOrder(User buyingUser, Order buyOrder) {
        long totalPrice = buyOrder.getPrice() * buyOrder.getQuantity();
        if(totalPrice > buyingUser.getBalance().get()){
            buyOrder.setOrderStatus(OrderStatus.CANCELLED);
            throw new IllegalStateException("User "+buyingUser.getName()+" does not enough balance");
        }
    }
}
