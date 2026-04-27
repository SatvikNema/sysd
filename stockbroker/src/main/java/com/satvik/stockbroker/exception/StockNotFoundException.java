package com.satvik.stockbroker.exception;

public class StockNotFoundException extends RuntimeException{
    public StockNotFoundException(String msg){
        super(msg);
    }
}
