package com.satvik.omsclaude.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {
    private final String id;
    private final String name;
    private double balance;
}
