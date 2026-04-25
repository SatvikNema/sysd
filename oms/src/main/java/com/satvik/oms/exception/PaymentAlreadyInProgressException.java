package com.satvik.oms.exception;

public class PaymentAlreadyInProgressException extends RuntimeException{
    public PaymentAlreadyInProgressException(String msg){
        super(msg);
    }
}
