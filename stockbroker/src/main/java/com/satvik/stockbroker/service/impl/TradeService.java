package com.satvik.stockbroker.service.impl;

import com.satvik.stockbroker.entity.Trade;
import com.satvik.stockbroker.service.ITradeService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TradeService implements ITradeService {
    private final Map<String, List<Trade>> userIdToTrade;

    public TradeService(){
        this.userIdToTrade = new ConcurrentHashMap<>();
    }
    @Override
    public void addTrade(Trade trade) {
        userIdToTrade.computeIfAbsent(trade.getBuyId(), k -> new ArrayList<>()).add(trade);
        userIdToTrade.computeIfAbsent(trade.getSellId(), k -> new ArrayList<>()).add(trade);
    }

    @Override
    public List<Trade> getTrades(String userId) {
        List<Trade> trades = userIdToTrade.getOrDefault(userId, Collections.emptyList());
        return new ArrayList<>(trades);
    }
}
