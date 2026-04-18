package com.satvik.sysd.parkinglot.repository;

import java.util.UUID;

public interface Repository<T, ID> {
    T save(T t);
    T findById(ID id);
    default void init(){}
    T removeById(ID id);
}
