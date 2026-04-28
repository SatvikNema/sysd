package com.satvik.stockbroker.model;

import com.satvik.stockbroker.entity.Stock;

public interface Observer<T> {
    void update(T t);
}
