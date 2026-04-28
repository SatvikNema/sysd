package com.satvik.stockbroker.service;

import com.satvik.stockbroker.entity.Order;
import com.satvik.stockbroker.entity.OrderStatus;
import com.satvik.stockbroker.entity.OrderType;
import com.satvik.stockbroker.entity.PortfolioItem;
import com.satvik.stockbroker.entity.Stock;
import com.satvik.stockbroker.entity.Trade;
import com.satvik.stockbroker.entity.User;
import com.satvik.stockbroker.service.impl.TradeService;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static com.satvik.stockbroker.util.OrderValidator.validateBuyOrder;
import static com.satvik.stockbroker.util.OrderValidator.validateSellOrder;

public class OrderFulfillmentService {

    private final TradeService tradeService;

    public OrderFulfillmentService(TradeService tradeService){
        this.tradeService = tradeService;
    }


    public synchronized void fullFillOrders(Order biggerOrder, List<Order> smallerOrders){
        for(Order order : smallerOrders){
            if(biggerOrder.getQuantity() == biggerOrder.getQuantityFilled()) {
                break;
            }
            Order sellOrder;
            Order buyOrder;
            if(biggerOrder.getOrderType() == OrderType.BUY){
                buyOrder = biggerOrder;
                sellOrder = order;
            } else if(biggerOrder.getOrderType() == OrderType.SELL){
                sellOrder = biggerOrder;
                buyOrder = order;
            } else {
                throw new IllegalStateException("Unknown order type");
            }
            fullFillOneOrder(sellOrder, buyOrder);
        }
    }

    public synchronized void fullFillOneOrder(Order sellOrder, Order buyOrder){
        User sellingUser = sellOrder.getUser();
        User buyingUser = buyOrder.getUser();
        long sellingPrice = sellOrder.getPrice();
        validateSellOrder(sellingUser, sellOrder);
        validateBuyOrder(buyingUser, buyOrder);

        Stock stock = sellOrder.getStock();

        int sellingQuantity = sellOrder.remainingQuantity();
        int buyingQuantity = buyOrder.remainingQuantity();
        if(!stock.getId().equals(buyOrder.getStock().getId())){
            throw new IllegalStateException("Stocks in both orders should be same");
        }

        int partialFilledOrderQuantity;
        OrderType typeToMarkPartial = null;
        if(buyingQuantity < sellingQuantity){
            partialFilledOrderQuantity = buyingQuantity;
            typeToMarkPartial = OrderType.SELL;

        } else if(buyingQuantity > sellingQuantity) {
            partialFilledOrderQuantity = sellingQuantity;
            typeToMarkPartial = OrderType.BUY;
        } else {
            partialFilledOrderQuantity = sellingQuantity;
        }

        PortfolioItem boughtItem = PortfolioItem.builder()
                .userId(buyingUser.getId())
                .stock(stock)
                .qty(partialFilledOrderQuantity)
                .averagePrice(sellingPrice)
                .build();
        addToUserPortfolio(buyingUser, boughtItem);

        PortfolioItem soldItem = PortfolioItem.builder()
                .userId(sellingUser.getId())
                .stock(stock)
                .qty(partialFilledOrderQuantity)
                .averagePrice(sellingPrice)
                .build();
        removeFromUserPortfolio(sellingUser, soldItem);

        long totalSellingPrice = sellingPrice * partialFilledOrderQuantity;
        sellingUser.getBalance().getAndAdd(totalSellingPrice);
        buyingUser.getBalance().getAndAdd(-totalSellingPrice);
        buyOrder.setQuantityFilled(buyOrder.getQuantityFilled() + partialFilledOrderQuantity);
        sellOrder.setQuantityFilled(sellOrder.getQuantityFilled() + partialFilledOrderQuantity);

        if(typeToMarkPartial == OrderType.BUY){
            sellOrder.setOrderStatus(OrderStatus.FULFILLED);
            buyOrder.setOrderStatus(OrderStatus.PARTIALLY_FULFILLED);
            if(buyOrder.getQuantityFilled() == buyOrder.getQuantity()){
                System.out.println("Partial Order completed "+buyOrder.getId());
                buyOrder.setOrderStatus(OrderStatus.FULFILLED);
            }
            stock.getOrderQueue().remove(OrderType.SELL, sellOrder);
        } else if(typeToMarkPartial == OrderType.SELL){
            sellOrder.setOrderStatus(OrderStatus.PARTIALLY_FULFILLED);
            if(sellOrder.getQuantityFilled() == sellOrder.getQuantity()){
                System.out.println("Partial Order completed "+sellOrder.getId());
                sellOrder.setOrderStatus(OrderStatus.FULFILLED);
            }
            buyOrder.setOrderStatus(OrderStatus.FULFILLED);
            stock.getOrderQueue().remove(OrderType.BUY, buyOrder);
        } else {
            sellOrder.setOrderStatus(OrderStatus.FULFILLED);
            buyOrder.setOrderStatus(OrderStatus.FULFILLED);
            stock.getOrderQueue().removeTuple(sellOrder, buyOrder);
        }
        stock.getCurrentPrice().set(sellingPrice);
        Trade trade = Trade.builder()
                .id(UUID.randomUUID().toString())
                .sellId(sellingUser.getId())
                .buyId(buyingUser.getId())
                .price(sellOrder.getPrice())
                .quantity(partialFilledOrderQuantity)
                .executedAt(Instant.now())
                .build();
        tradeService.addTrade(trade);

        System.out.println("Price of "+stock.getName()+" set to "+sellingPrice);
    }



    public void addToUserPortfolio(User user, PortfolioItem portfolioItem){
        List<PortfolioItem> portfolioItems = user.getPortfolio();
        portfolioItems
                .stream()
                .filter(p -> portfolioItem.getStock().getId().equals(p.getStock().getId()))
                .findFirst()
                .ifPresentOrElse(existingOwnership -> {
                    int existingQuantity = existingOwnership.getQty();
                    double existingAverage = existingOwnership.getAveragePrice();

                    int newQuantity = existingQuantity + portfolioItem.getQty();
                    double newTotalPrice = (existingAverage * existingQuantity) + (portfolioItem.getAveragePrice() * portfolioItem.getQty());

                    existingOwnership.setQty(newQuantity);
                    existingOwnership.setAveragePrice(newTotalPrice / newQuantity);
                }, () -> portfolioItems.add(portfolioItem));

    }

    public void removeFromUserPortfolio(User user, PortfolioItem sellItem) {
        List<PortfolioItem> portfolioItems = user.getPortfolio();

        portfolioItems.stream()
                .filter(p -> sellItem.getStock().getId().equals(p.getStock().getId()))
                .findFirst()
                .ifPresent(existingOwnership -> {
                    int existingQty = existingOwnership.getQty();
                    int sellQty = sellItem.getQty();
                    if (sellQty > existingQty) {
                        throw new IllegalArgumentException("Cannot sell more than owned quantity");
                    }
                    int remainingQty = existingQty - sellQty;
                    if (remainingQty == 0) {
                        // remove the stock completely from portfolio
                        portfolioItems.remove(existingOwnership);
                    } else {
                        existingOwnership.setQty(remainingQty);
                    }
                });
    }
}
