package com.satvik.splitwise.service;

import com.satvik.splitwise.entity.Expense;
import com.satvik.splitwise.model.SplitInformation;
import com.satvik.splitwise.strategy.SplitStrategy;

public interface IExpenseService {
    Expense addExpense(SplitInformation splitInformation, SplitStrategy splitStrategy);

    void settleExpense(String expenseId, String userName, double amount);
}
