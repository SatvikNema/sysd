package com.satvik.stockbroker.entity;

import javax.management.InvalidAttributeValueException;

public enum OrderType {
    BUY,
    SELL;

    public static OrderType fromValue(String value){
        for (OrderType orderType : values()) {
            if(value.equalsIgnoreCase(orderType.toString())){
                return orderType;
            }
        }
        throw new IllegalStateException("no order type available for "+value);
    }
}
