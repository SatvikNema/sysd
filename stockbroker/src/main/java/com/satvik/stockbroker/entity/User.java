package com.satvik.stockbroker.entity;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Data
@Builder
@EqualsAndHashCode(of = "id")
public class User {
    private String id;
    private String name;
    private AtomicLong balance;
    private List<PortfolioItem> portfolio;
}
