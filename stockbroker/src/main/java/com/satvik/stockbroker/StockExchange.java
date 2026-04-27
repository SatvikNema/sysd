package com.satvik.stockbroker;

import com.satvik.stockbroker.entity.OrderType;
import com.satvik.stockbroker.entity.PortfolioItem;
import com.satvik.stockbroker.entity.Stock;
import com.satvik.stockbroker.entity.User;
import com.satvik.stockbroker.service.IOrderService;
import com.satvik.stockbroker.service.IStockService;
import com.satvik.stockbroker.service.IUserService;
import com.satvik.stockbroker.service.OrderExecutorService;
import com.satvik.stockbroker.service.impl.OrderService;
import com.satvik.stockbroker.service.impl.StockService;
import com.satvik.stockbroker.service.impl.UserService;

import java.time.Clock;
import java.util.List;

public class StockExchange {

    public static final String ITC = "ITC";
    public static final String HUL = "HUL";
    public static final String MRF = "MRF";

    public static void main(String[] args) throws InterruptedException {
        IUserService userService = new UserService();
        IStockService stockService = new StockService();
        IOrderService orderService = new OrderService(userService, stockService, Clock.systemUTC());
        OrderExecutorService orderExecutorService = new OrderExecutorService(orderService);
//        orderExecutorService.start();

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
                PortfolioItem.builder().userId(satvik.getId()).stock(itc).qty(5).averagePrice(100_00).build(),
                PortfolioItem.builder().userId(satvik.getId()).stock(hul).qty(5).averagePrice(200_00).build(),
                PortfolioItem.builder().userId(satvik.getId()).stock(mrf).qty(5).averagePrice(1000_00).build()
        );
        lakshita.setPortfolio(lakshitaPortfolio);

        List<PortfolioItem> keshavPortfolio = List.of(
                PortfolioItem.builder().userId(satvik.getId()).stock(itc).qty(50).averagePrice(100_00).build(),
                PortfolioItem.builder().userId(satvik.getId()).stock(hul).qty(50).averagePrice(200_00).build(),
                PortfolioItem.builder().userId(satvik.getId()).stock(mrf).qty(50).averagePrice(1000_00).build()
        );
        keshav.setPortfolio(keshavPortfolio);


        orderService.placeOrder("Kartik", OrderType.BUY, "ITC", 6, 250_00);
        orderService.placeOrder("Satvik", OrderType.SELL, "ITC", 2, 235_00);
        orderService.placeOrder("Satvik", OrderType.SELL, "ITC", 2, 240_00);
        orderService.placeOrder("Satvik", OrderType.SELL, "ITC", 2, 245_00);

        Thread.sleep(3000);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                orderExecutorService.shutDown();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }));
    }
}
