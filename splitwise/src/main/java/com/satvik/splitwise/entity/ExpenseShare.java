package com.satvik.splitwise.entity;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExpenseShare {
    private User user;
    private double amount;
}
