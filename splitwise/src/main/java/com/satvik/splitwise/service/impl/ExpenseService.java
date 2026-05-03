package com.satvik.splitwise.service.impl;

import com.satvik.splitwise.entity.Expense;
import com.satvik.splitwise.entity.ExpenseShare;
import com.satvik.splitwise.entity.Group;
import com.satvik.splitwise.entity.User;
import com.satvik.splitwise.exception.ExpenseNotFoundException;
import com.satvik.splitwise.model.Graph;
import com.satvik.splitwise.model.SplitInformation;
import com.satvik.splitwise.service.IExpenseService;
import com.satvik.splitwise.service.IGroupService;
import com.satvik.splitwise.strategy.SplitStrategy;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ExpenseService implements IExpenseService {

    private final IGroupService groupService;
    private final Map<String, Expense> expenseMap;

    public ExpenseService(IGroupService groupService){
        this.groupService = groupService;
        this.expenseMap = new ConcurrentHashMap<>();
    }

    @Override
    public Expense addExpense(SplitInformation splitInformation, SplitStrategy splitStrategy) {
        String id = UUID.randomUUID().toString();
        Group group = splitInformation.getGroup();
        groupService.getGroup(group.getId());
        List<ExpenseShare> expenseShares = splitStrategy.getShares(splitInformation);
        Expense expense = Expense.builder()
                .id(id)
                .groupId(group.getId())
                .description(splitInformation.getDescription())
                .amount(splitInformation.getAmount())
                .paidBy(splitInformation.getPaidBy())
                .expenseShares(expenseShares)
                .build();

        expenseMap.put(id, expense);
        addExpense(group, expense);
        return expense;
    }

    @Override
    public void settleExpense(String expenseId, String userName, double amount) {
        if(amount < 0){
            throw new IllegalStateException("Amount to settle should be positive");
        }

        Expense expense = expenseMap.get(expenseId);
        if(expense == null){
            throw new IllegalArgumentException("Expense with id " + expenseId + " does not exist");
        }

        ExpenseShare expenseShare = expense.getExpenseShares()
                .stream()
                .filter(e -> e.getUser().getName().equals(userName))
                .findFirst().orElseThrow(() -> new ExpenseNotFoundException(
                        "Expense share not found for user "+userName+" in expense "+expenseId));

        String groupId = expense.getGroupId();
        Group group = groupService.getGroup(groupId);
        group.getUserGraph().addEdge(expenseShare.getUser(), expense.getPaidBy(), -amount);
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
            group.getUserGraph().addEdge(debtUser, paidByUser, debtAmount);
        }
    }
}
