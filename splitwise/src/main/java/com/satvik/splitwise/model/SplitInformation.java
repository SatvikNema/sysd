package com.satvik.splitwise.model;

import com.satvik.splitwise.entity.Group;
import com.satvik.splitwise.entity.User;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class SplitInformation {
    @NonNull
    private User paidBy;

    @NonNull
    private Group group;
    private String description;
    private double amount;

    @NonNull
    private List<User> users;
    private Map<User, Double> amountShare;
    private Map<User, Double> percentageShare;
}
