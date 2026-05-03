package com.satvik.splitwise.exception;

public class ExpenseNotFoundException extends RuntimeException {
    public ExpenseNotFoundException(){
        super();
    }

    public ExpenseNotFoundException(String msg){
        super(msg);
    }
}
