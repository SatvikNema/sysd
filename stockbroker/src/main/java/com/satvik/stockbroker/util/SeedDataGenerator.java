package com.satvik.stockbroker.util;

import com.satvik.stockbroker.entity.PortfolioItem;
import com.satvik.stockbroker.entity.Stock;
import com.satvik.stockbroker.entity.User;
import com.satvik.stockbroker.service.IStockService;
import com.satvik.stockbroker.service.IUserService;

import java.util.List;

public class SeedDataGenerator {

    public static final String ITC = "ITC";
    public static final String HUL = "HUL";
    public static final String MRF = "MRF";

    public static void generate(IUserService userService, IStockService stockService){
        Stock itc = stockService.addStock(ITC, 100_00);
        Stock hul = stockService.addStock(HUL, 200_00);
        Stock mrf = stockService.addStock(MRF, 1000_00);

        User satvik = userService.addUser("Satvik");
        userService.addBalance(satvik, 10000_00);

        User kartik = userService.addUser("Kartik");
        userService.addBalance(kartik, 10000_00);

        User lakshita = userService.addUser("Lakshita");
        userService.addBalance(lakshita, 10000_00);

        User keshav = userService.addUser("Keshav");
        userService.addBalance(keshav, 10000_00);

        List<PortfolioItem> satvikPortfolio = List.of(
                PortfolioItem.builder().userId(satvik.getId()).stock(itc).qty(10).averagePrice(100_00).build(),
                PortfolioItem.builder().userId(satvik.getId()).stock(hul).qty(20).averagePrice(200_00).build(),
                PortfolioItem.builder().userId(satvik.getId()).stock(mrf).qty(30).averagePrice(1000_00).build()
        );

        satvik.setPortfolio(satvikPortfolio);

        List<PortfolioItem> lakshitaPortfolio = List.of(
                PortfolioItem.builder().userId(lakshita.getId()).stock(itc).qty(5).averagePrice(100_00).build(),
                PortfolioItem.builder().userId(lakshita.getId()).stock(hul).qty(5).averagePrice(200_00).build(),
                PortfolioItem.builder().userId(lakshita.getId()).stock(mrf).qty(5).averagePrice(1000_00).build()
        );
        lakshita.setPortfolio(lakshitaPortfolio);

        List<PortfolioItem> keshavPortfolio = List.of(
                PortfolioItem.builder().userId(keshav.getId()).stock(itc).qty(50).averagePrice(100_00).build(),
                PortfolioItem.builder().userId(keshav.getId()).stock(hul).qty(50).averagePrice(200_00).build(),
                PortfolioItem.builder().userId(keshav.getId()).stock(mrf).qty(50).averagePrice(1000_00).build()
        );
        keshav.setPortfolio(keshavPortfolio);

        // adding observers
        List<User> users = List.of(satvik, lakshita, kartik, keshav);
        List<Stock> stocks = List.of(itc, hul, mrf);
        stocks.forEach(stock -> users.forEach(user -> {
            StockPriceChangeNotifier notifier = new StockPriceChangeNotifier(user);
            stock.addObserver(notifier);
        }));
    }
}
