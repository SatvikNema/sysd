package com.satvik.stockbroker.service.impl;

import com.satvik.stockbroker.entity.Stock;
import com.satvik.stockbroker.model.OrderQueue;
import com.satvik.stockbroker.service.IStockService;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class StockService implements IStockService {
    private final Map<String, Stock> stockIdToStock;
    public StockService(){
        this.stockIdToStock = new ConcurrentHashMap<>();
    }

    @Override
    public Stock addStock(String name, long currentPrice) {
        Stock stock = Stock.builder()
                .id(name)
                .name(name)
                .currentPrice(new AtomicLong(currentPrice))
                .orderQueue(new OrderQueue())
                .build();
        stockIdToStock.put(name, stock);
        return stock;
    }

    @Override
    public Optional<Stock> getStock(String id) {
        return Optional.ofNullable(stockIdToStock.get(id));
    }
}
