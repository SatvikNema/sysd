package com.satvik.splitwise.entity;

import lombok.Builder;
import lombok.Data;

import java.util.List;


@Data
@Builder
public class Expense {
    private String id;
    private String groupId;
    private String description;
    private double amount;
    private User paidBy;
    private List<ExpenseShare> expenseShares;
}
