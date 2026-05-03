package com.satvik.splitwise.service.impl;

import com.satvik.splitwise.entity.Expense;
import com.satvik.splitwise.entity.ExpenseShare;
import com.satvik.splitwise.entity.Group;
import com.satvik.splitwise.entity.User;
import com.satvik.splitwise.model.SplitInformation;
import com.satvik.splitwise.service.IExpenseService;
import com.satvik.splitwise.service.IGroupService;
import com.satvik.splitwise.strategy.SplitStrategy;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ExpenseService implements IExpenseService {

    private final IGroupService groupService;

    public ExpenseService(IGroupService groupService){
        this.groupService = groupService;
    }

    @Override
    public Expense addExpense(SplitInformation splitInformation, SplitStrategy splitStrategy) {
        String id = UUID.randomUUID().toString();
        Group group = splitInformation.getGroup();
        Expense expense;
        synchronized (group) {
            groupService.getGroup(group.getId());
            List<ExpenseShare> expenseShares = splitStrategy.getShares(splitInformation);
            expense = Expense.builder()
                    .id(id)
                    .groupId(group.getId())
                    .description(splitInformation.getDescription())
                    .amount(splitInformation.getAmount())
                    .paidBy(splitInformation.getPaidBy())
                    .expenseShares(expenseShares)
                    .build();

            addExpense(group, expense);
        }
        return expense;
    }

    @Override
    public void settle(Group group, User debtUser, User toBePaidUser, double amount) {
        if(amount <= 0){
            throw new IllegalStateException("Amount to settle should be positive");
        }
        synchronized (group) {
            Map<User, Map<User, Double>> userGraph = group.getUserGraph();
            Double ownedAmount = userGraph
                    .computeIfAbsent(debtUser, k -> new ConcurrentHashMap<>())
                    .getOrDefault(toBePaidUser, 0d);
            if (amount > ownedAmount) {
                throw new IllegalStateException("Amount to settle should be less than or equal to the amount owed");
            }
            group.addOweAmount(debtUser, toBePaidUser, -amount);
        }
    }

    private void addExpense(Group group, Expense expense){
        group.getExpenses().add(expense);

        for(ExpenseShare expenseShare : expense.getExpenseShares()){
            User paidByUser = expense.getPaidBy();
            User debtUser = expenseShare.getUser();
            if(paidByUser.equals(debtUser)) continue;
            double debtAmount = -expenseShare.getAmount();
            if(debtAmount <= 0){
                throw new IllegalStateException("Edge value should be positive");
            }
            group.addOweAmount(debtUser, paidByUser, debtAmount);
        }
    }
}
