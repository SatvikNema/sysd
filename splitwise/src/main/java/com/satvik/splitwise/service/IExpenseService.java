package com.satvik.splitwise.service;

import com.satvik.splitwise.entity.Expense;
import com.satvik.splitwise.entity.Group;
import com.satvik.splitwise.entity.User;
import com.satvik.splitwise.model.SplitInformation;
import com.satvik.splitwise.strategy.SplitStrategy;

public interface IExpenseService {
    Expense addExpense(SplitInformation splitInformation, SplitStrategy splitStrategy);

    void settle(Group group, User debtUser, User toBePaidUser, double amount);
}
