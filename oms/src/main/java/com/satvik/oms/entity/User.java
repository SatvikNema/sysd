package com.satvik.oms.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {
    private String id;
    private double balance;
    private String name;
}
