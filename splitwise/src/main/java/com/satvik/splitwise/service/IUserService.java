package com.satvik.splitwise.service;

import com.satvik.splitwise.entity.User;

public interface IUserService {
    User createUser(String name);
    User getUser(String name);
}
