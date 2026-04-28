package com.satvik.stockbroker.util;

import com.satvik.stockbroker.entity.Stock;
import com.satvik.stockbroker.entity.User;
import com.satvik.stockbroker.model.Observer;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StockPriceChangeNotifier implements Observer<Stock> {
    private User user;

    @Override
    public void update(Stock stock) {
        System.out.println("User " + user.getName() + " notified of price change for stock " + stock.getName() + ": new price = " + stock.getCurrentPrice());
    }
}
