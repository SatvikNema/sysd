package com.satvik.splitwise.strategy;

import com.satvik.splitwise.entity.ExpenseShare;
import com.satvik.splitwise.model.SplitInformation;

import java.util.List;

public interface SplitStrategy {
    List<ExpenseShare> getShares(SplitInformation splitInformation);
}
