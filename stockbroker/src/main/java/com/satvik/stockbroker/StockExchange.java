package com.satvik.stockbroker;

import com.satvik.stockbroker.entity.Order;
import com.satvik.stockbroker.entity.OrderType;
import com.satvik.stockbroker.entity.PortfolioItem;
import com.satvik.stockbroker.entity.Stock;
import com.satvik.stockbroker.entity.User;
import com.satvik.stockbroker.exception.UserNotFoundException;
import com.satvik.stockbroker.service.IOrderService;
import com.satvik.stockbroker.service.IStockService;
import com.satvik.stockbroker.service.IUserService;
import com.satvik.stockbroker.service.impl.OrderService;
import com.satvik.stockbroker.service.impl.StockService;
import com.satvik.stockbroker.service.impl.UserService;

import java.time.Clock;
import java.util.List;
import java.util.Scanner;

public class StockExchange {

    public static final String ITC = "ITC";
    public static final String HUL = "HUL";
    public static final String MRF = "MRF";

    public static void main(String[] args) throws InterruptedException {
        IUserService userService = new UserService();
        IStockService stockService = new StockService();
        IOrderService orderService = new OrderService(userService, stockService, Clock.systemUTC());


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

//        Order sell1 = orderService.placeOrder("Satvik", OrderType.SELL, "ITC", 2, 245_00);
//        Order sell2 = orderService.placeOrder("Satvik", OrderType.SELL, "ITC", 2, 240_00);
//        Order sell3 = orderService.placeOrder("Satvik", OrderType.SELL, "ITC", 2, 235_00);
//
//        Order buy1 = orderService.placeOrder("Kartik", OrderType.BUY, "ITC", 6, 250_00);
//        orderService.matchOrder(buy1.getId());

        Scanner scanner = new Scanner(System.in);


        while (true) {
            System.out.print("Enter command (type 'exit' to quit):\n");
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("exit")) {
                break;
            }

            String[] inputs = input.split(" ");

            if("order".equalsIgnoreCase(inputs[0])){
                String user = inputs[1];
                String stockId = inputs[2];
                OrderType orderType = OrderType.fromValue(inputs[3]);
                int qty = Integer.parseInt(inputs[4]);
                int price = Integer.parseInt(inputs[5]);
                Order order = orderService.placeOrder(user, orderType, stockId, qty, price);
                System.out.println("Order placed: "+order.getId());
            } else if("execute".equalsIgnoreCase(inputs[0])){
                String orderId = inputs[1];
                orderService.executeOrder(orderId);
            } else if("portfolio".equalsIgnoreCase(inputs[0])){
                String userId = inputs[1];
                User user = userService.getUser(userId).orElseThrow(() -> new UserNotFoundException("No user with id "+userId));
                List<PortfolioItem> portfolio = user.getPortfolio();
                System.out.println("Stock, qty, averagePrice");
                for(PortfolioItem portfolioItem : portfolio){
                    String stock = portfolioItem.getStock().getId();
                    int qty = portfolioItem.getQty();
                    double averagePrice = portfolioItem.getAveragePrice();
                    System.out.println(stock+", "+qty+", "+averagePrice);
                }
            } else if("orders".equalsIgnoreCase(inputs[0])){
                String userId = inputs[1];
                User user = userService.getUser(userId).orElseThrow(() -> new UserNotFoundException("No user with id "+userId));
                List<Order> orders = orderService.getOrders(userId);
                System.out.println("Stock, qty, averagePrice");
                for(Order order : orders){
                    System.out.println(order);
                }
            }
        }

        /*
order Satvik ITC sell 2 24500
order Satvik ITC sell 2 24000
order Satvik ITC sell 2 23500
order Kartik ITC buy 6 25000

execute <prev id>
portfolio Satvik
portfolio Kartik
orders Satvik
orders Kartik
         */

/*
        OrderExecutorService orderExecutorService = new OrderExecutorService(orderService);
        orderExecutorService.start();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                orderExecutorService.shutDown();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }));
 */
    }
}
