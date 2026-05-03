package com.satvik.splitwise.strategy;

import com.satvik.splitwise.entity.ExpenseShare;
import com.satvik.splitwise.entity.User;
import com.satvik.splitwise.model.SplitInformation;

import java.util.ArrayList;
import java.util.List;

public class EqualAmountSplitStrategy implements SplitStrategy{

    /**
     *
     * @param splitInformation - the users list does not include the payer
     * @return
     */
    @Override
    public List<ExpenseShare> getShares(SplitInformation splitInformation) {
        int numberOfDebtUsers = splitInformation.getUsers().size();
        int totalUsers = numberOfDebtUsers + 1;
        double amount = splitInformation.getAmount();
        double split = amount / totalUsers;
        double filledMinusOne = split * (totalUsers - 1);
        double lastSplit = amount - filledMinusOne;

        List<ExpenseShare> shares = new ArrayList<>();
        shares.add(ExpenseShare.builder().user(splitInformation.getPaidBy()).amount(amount).build());
        for(int i = 0 ; i< numberOfDebtUsers - 1 ; i++){
            User user = splitInformation.getUsers().get(i);
            shares.add(ExpenseShare.builder().user(user).amount(-split).build());
        }
        shares.add(ExpenseShare
                .builder()
                .user(splitInformation.getUsers().get(numberOfDebtUsers - 1))
                .amount(-lastSplit)
                .build());
        return shares;
    }
}
