package com.satvik.stockbroker.service.impl;

import com.satvik.stockbroker.entity.Trade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TradeService implements ITradeService {
    private final Map<String, List<Trade>> tradeIdToTrade;

    public TradeService(){
        this.tradeIdToTrade = new ConcurrentHashMap<>();
    }
    @Override
    public void addTrade(Trade trade) {
        tradeIdToTrade.getOrDefault(trade.getBuyId(), new ArrayList<>()).add(trade);
        tradeIdToTrade.getOrDefault(trade.getSellId(), new ArrayList<>()).add(trade);
    }

    @Override
    public List<Trade> getTrades(String userId) {
        return tradeIdToTrade.getOrDefault(userId, Collections.emptyList());
    }
}
