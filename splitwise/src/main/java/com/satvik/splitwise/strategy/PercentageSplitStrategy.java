package com.satvik.splitwise.strategy;

import com.satvik.splitwise.entity.ExpenseShare;
import com.satvik.splitwise.entity.User;
import com.satvik.splitwise.model.SplitInformation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PercentageSplitStrategy implements SplitStrategy{

    /**
     *
     * @param splitInformation - the users list does not include the payer
     *                         - But the map of shares %s does include everyone for the shares %s to sum upto 100
     * @return
     */
    @Override
    public List<ExpenseShare> getShares(SplitInformation splitInformation) {
        int numberOfDebtUsers = splitInformation.getUsers().size();
        double amount = splitInformation.getAmount();
        Map<User, Double> split = splitInformation.getPercentageShare();
        if((numberOfDebtUsers+1) != split.size()){
            throw new IllegalArgumentException("Number of users and percentage split size should be same");
        }
        double accountedFor = 0;

        List<ExpenseShare> shares = new ArrayList<>();
        shares.add(ExpenseShare.builder().user(splitInformation.getPaidBy()).amount(amount).build());
        Set<User> users = new HashSet<>(split.keySet());
        for(int i = 0 ; i < numberOfDebtUsers; i++){
            User user = splitInformation.getUsers().get(i);

            Double splitPercentage = split.get(user);
            double splitValue = (amount * splitPercentage) / 100;
            accountedFor += splitPercentage;
            shares.add(ExpenseShare.builder().user(user).amount(-splitValue).build());
            users.remove(user);
        }

        Double paidUserShare = split.get(splitInformation.getPaidBy());
        if(paidUserShare == null || users.size() != 1){
            throw new IllegalArgumentException("Percentage split should be provided for all users, including the payer");
        }

        double totalPercentage = accountedFor + paidUserShare;
        if(Math.abs(totalPercentage - 100) > 1e-6){
            throw new IllegalArgumentException("Total percentage distribution should be 100. Not "+totalPercentage);
        }

        return shares;
    }
}
