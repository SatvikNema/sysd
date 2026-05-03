package com.satvik.splitwise.entity;

import com.satvik.splitwise.model.Graph;
import com.satvik.splitwise.model.Node;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Builder
@EqualsAndHashCode(of = "id")
public class Group {
    private String id;
    private final Set<User> users = new HashSet<>();
    private User createdBy;
    private final List<Expense> expenses = new ArrayList<>();
    private final Graph<User> userGraph = new Graph<>();

    public synchronized void addUser(User user){
        if(users.contains(user)) return;
        users.add(user);
        userGraph.addNode(new Node<>(user));
    }

}
