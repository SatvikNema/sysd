package com.satvik.stockbroker.service.impl;

import com.satvik.stockbroker.entity.User;
import com.satvik.stockbroker.exception.UserNotFoundException;
import com.satvik.stockbroker.service.IUserService;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class UserService implements IUserService {
    private final Map<String, User> userIdToUser;

    public UserService(){
        this.userIdToUser = new ConcurrentHashMap<>();
    }

    @Override
    public User addUser(String name) {
        String id = name;
        User user = User.builder()
                .id(id)
                .name(name)
                .balance(new AtomicLong(0))
                .portfolio(new ArrayList<>())
                .build();
        userIdToUser.put(id, user);
        return user;
    }

    @Override
    public Optional<User> getUser(String userId) {
        return Optional.ofNullable(userIdToUser.get(userId));
    }

    @Override
    public User addBalance(User user, long balance) {
        User userInExchange = userIdToUser.get(user.getId());
        if(userInExchange == null){
            throw new UserNotFoundException("User with id " + user.getId() + " not found");
        }
        userInExchange.getBalance().getAndAdd(balance);
        return userInExchange;
    }
}
