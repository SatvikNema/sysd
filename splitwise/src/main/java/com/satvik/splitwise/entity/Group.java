package com.satvik.splitwise.entity;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Data
@Builder
@EqualsAndHashCode(of = "id")
public class Group {
    private String id;
    private final Set<User> users = new HashSet<>();
    private User createdBy;
    private final List<Expense> expenses = new ArrayList<>();

    private final Map<User, Map<User, Double>> userGraph = new ConcurrentHashMap<>();

    public synchronized void addUser(User user){
        if(users.contains(user)) return;
        users.add(user);
        userGraph.putIfAbsent(user, new ConcurrentHashMap<>());
    }

    public void addOweAmount(User debtUser, User toBePaidUser, double amount){
        userGraph
                .computeIfAbsent(debtUser, (user) -> new ConcurrentHashMap<>())
                .merge(toBePaidUser, amount, Double::sum);
    }

}
