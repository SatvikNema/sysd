package com.satvik.stockbroker.service;

import com.satvik.stockbroker.entity.Stock;

import java.util.Optional;

public interface IStockService {
    Stock addStock(String name, long currentPrice);
    Optional<Stock> getStock(String id);
}
