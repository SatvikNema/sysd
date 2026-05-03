package com.satvik.splitwise.service;

import com.satvik.splitwise.entity.Expense;
import com.satvik.splitwise.entity.Group;
import com.satvik.splitwise.entity.User;

import java.util.List;
import java.util.Optional;

public interface IGroupService {
    /**
     *
     * @param creator The group creator
     * @param users The members of the group
     * @param name Name of the group
     * @return The created Group
     */
    Group createGroup(User creator, List<User> users, String name);
    Group addUser(Group group, User user);
    Group getGroup(String groupId);

    List<Expense> getTransactionHistory(Group group, Optional<User> userOptional);
}
