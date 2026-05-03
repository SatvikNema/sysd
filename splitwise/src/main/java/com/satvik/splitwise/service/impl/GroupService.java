package com.satvik.splitwise.service.impl;

import com.satvik.splitwise.entity.Expense;
import com.satvik.splitwise.entity.Group;
import com.satvik.splitwise.entity.User;
import com.satvik.splitwise.exception.GroupNotFoundException;
import com.satvik.splitwise.service.IGroupService;
import com.satvik.splitwise.service.IUserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GroupService implements IGroupService {
    private final Map<String, Group> groupMap;
    private final IUserService userService;

    public GroupService(IUserService userService){
        this.groupMap = new ConcurrentHashMap<>();
        this.userService = userService;
    }

    @Override
    public Group createGroup(User creator, List<User> users, String name) {
        String id = UUID.randomUUID().toString();
        userService.getUser(creator.getName());

        Group group = Group
                .builder()
                .id(id)
                .createdBy(creator)
                .build();
        group.addUser(creator);
        users
            .stream()
            .map(e -> userService.getUser(e.getName()))
            .forEach(group::addUser);
        groupMap.put(id, group);
        return group;
    }

    @Override
    public Group addUser(Group group, User user) {
        Group group1 = getGroup(group.getId());
        group1.addUser(user);
        return group1;
    }

    @Override
    public Group getGroup(String groupId) {
        Group group = groupMap.get(groupId);
        if(group == null){
            throw new GroupNotFoundException("Group not found with id "+groupId);
        }
        return group;
    }


    @Override
    public List<Expense> getTransactionHistory(Group group) {
        return new ArrayList<>(group.getExpenses());
    }

    @Override
    public List<Expense> getTransactionHistory(Group group, User user) {
        return getTransactionHistory(group).stream()
                    .filter(e ->
                            e.getPaidBy().equals(user) ||
                                    e.getExpenseShares().stream().anyMatch(s -> s.getUser().equals(user)))
                    .toList();
    }

    @Override
    public double getBalance(Group group, User a, User b) {
        double aOwsB = group.getUserGraph()
                .getOrDefault(a, Map.of())
                .getOrDefault(b, 0.0);

        double bOwsA = group.getUserGraph()
                .getOrDefault(b, Map.of())
                .getOrDefault(a, 0.0);

        return aOwsB - bOwsA;
    }
}
