package com.satvik.stockbroker.service;

import com.satvik.stockbroker.entity.User;

import java.util.Optional;

public interface IUserService {
    User addUser(String name);
    User addBalance(User user, long balance);
    Optional<User> getUser(String userId);
}
