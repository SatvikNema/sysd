package com.satvik.splitwise;

import com.satvik.splitwise.entity.Expense;
import com.satvik.splitwise.entity.Group;
import com.satvik.splitwise.entity.User;
import com.satvik.splitwise.model.Graph;
import com.satvik.splitwise.model.SplitInformation;
import com.satvik.splitwise.service.IExpenseService;
import com.satvik.splitwise.service.IGroupService;
import com.satvik.splitwise.service.IUserService;
import com.satvik.splitwise.service.impl.ExpenseService;
import com.satvik.splitwise.service.impl.GroupService;
import com.satvik.splitwise.service.impl.UserService;
import com.satvik.splitwise.strategy.EqualAmountSplitStrategy;
import com.satvik.splitwise.strategy.PercentageSplitStrategy;
import com.satvik.splitwise.strategy.SplitStrategy;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Driver {
    public void start(){
        IUserService userService = new UserService();
        IGroupService groupService = new GroupService(userService);
        IExpenseService expenseService = new ExpenseService(groupService);

        User satvik = userService.createUser("Satvik");
        User lakshita = userService.createUser("Lakshita");
        User kartik = userService.createUser("Kartik");
        User keshav = userService.createUser("Keshav");

        Group bandraOutingGroup = groupService
                .createGroup(satvik, List.of(lakshita, kartik, keshav), "Bandra Outing");

        SplitStrategy equalSplitStrategy = new EqualAmountSplitStrategy();
        SplitStrategy percentageSplitStrategy = new PercentageSplitStrategy();

        SplitInformation splitInformation = SplitInformation
                .builder()
                .group(bandraOutingGroup)
                .amount(1000)
                .users(List.of(lakshita, kartik, keshav))
                .paidBy(satvik)
                .description("Food at Veronica's")
                .build();
        Expense expense = expenseService.addExpense(splitInformation, equalSplitStrategy);

        splitInformation = SplitInformation
                .builder()
                .group(bandraOutingGroup)
                .amount(100)
                .users(List.of(lakshita, keshav))
                .paidBy(satvik)
                .description("Cab to Veronica's")
                .build();
        expense = expenseService.addExpense(splitInformation, equalSplitStrategy);

        splitInformation = SplitInformation
                .builder()
                .group(bandraOutingGroup)
                .amount(500)
                .users(List.of(satvik, keshav))
                .paidBy(lakshita)
                .description("Icecream at Rustom's")
                .build();
        expense = expenseService.addExpense(splitInformation, equalSplitStrategy);

        splitInformation = SplitInformation
                .builder()
                .group(bandraOutingGroup)
                .amount(2000)
                .users(List.of(kartik, lakshita, satvik))
                .paidBy(keshav)
                .description("Zoom car ride")
                .percentageShare(
                        Map.of(
                                kartik, 20d,
                                lakshita, 20d,
                                satvik, 25d,
                                keshav, 35d
                        )
                )
                .build();
        expense = expenseService.addExpense(splitInformation, percentageSplitStrategy);

        Graph<User> userGraph = bandraOutingGroup.getUserGraph();
        List<Expense> expenses = groupService.getTransactionHistory(bandraOutingGroup, Optional.of(kartik));

        expenseService.settleExpense(expense.getId(), kartik.getName(), 100);
        int x = 1;
    }
}
