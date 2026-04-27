package com.satvik.stockbroker;

public class Main {
    public static void main(String[] args) {
        System.out.println("hello");
    }
}

/**
 Create a stock broker Application
 Requirements:

 Add/Register users to the stock exchange system.
 Add stock to the stock exchange system.
 Each user should have a stock holding list(stock, price, qty) & balance.
 Users should be able to add balance to their account.
 Users should be able to buy/sell multiple stocks.
 Users should be able to check past successful trading

 Features:

 1. Traders place Buy and Sell orders for a stock indicating the price and quantity.
 2. Each order gets entered into the exchange’s order-book and remains there until it is matched.
    Order matching is attempted whenever a new order is added.
 3. The exchange follows a FirstInFirstOut Price-Time order-matching rule, which states that:
    “The first order in the order-book at a price level is the first order matched.
    All orders at the same price level are filled according to time priority”.
 4. The exchange works like a market where lower selling prices and higher buying prices get priority.
 5. A trade is executed when a buy price is greater than or equal to a sell price.
 6. The trade is recorded at the price of the sell order regardless of the price of the buy order.


 ============== my approach
 entities:
 user - id, name, balance, List(PortfolioItem), List(Order)
 PortfolioItem - userId, stock, qty, averagePrice
 Stock - id, name, currentPrice
 Order - userId, Stock, type(BUY/SELL), qty, targetPrice, createdAt, status (PENDING/FULFILLED/CANCELLED)

 services:
 OrderService - getOrders(User), placeOrder(User, Order), match(Order)
 UserService - addUser(name, balance), addBalance(User, amount)
 StockExchange -  addStock(Stock)

 callout -
 1. not writing separate data repositories. Will use in memory maps for each entity holder service.
 2. storing money as paisa. 1.09 rupee is stored as 109. To avoid floating point errors. Assuming lowest precision is 1 paisa.
 3. placeOrder method in orderService has 5 arguments. In practise, we would encapsulate it in a OrderRequest pojo
 */
