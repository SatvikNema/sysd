package com.satvik.splitwise.exception;

public class GroupNotFoundException extends RuntimeException {
    public GroupNotFoundException(){
        super();
    }

    public GroupNotFoundException(String msg){
        super(msg);
    }
}
