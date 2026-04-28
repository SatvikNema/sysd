package com.satvik.stockbroker.service.impl;

import com.satvik.stockbroker.entity.Trade;

import java.util.List;

public interface ITradeService {
    void addTrade(Trade trade);
    List<Trade> getTrades(String userId);
}
