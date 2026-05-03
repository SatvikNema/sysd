package com.satvik.splitwise.service.impl;

import com.satvik.splitwise.entity.User;
import com.satvik.splitwise.exception.UserNotFoundException;
import com.satvik.splitwise.service.IUserService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserService implements IUserService {
    private final Map<String, User> userMap;

    public UserService(){
        this.userMap = new ConcurrentHashMap<>();
    }

    @Override
    public User createUser(String name) {
        User user = User.builder().name(name).build();
        userMap.put(name, user);
        return user;
    }

    @Override
    public User getUser(String name) {
        User user = userMap.get(name);
        if(user == null){
            throw new UserNotFoundException("User "+name+" not found");
        }
        return user;
    }
}
