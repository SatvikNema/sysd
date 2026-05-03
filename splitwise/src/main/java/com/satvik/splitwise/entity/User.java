package com.satvik.splitwise.entity;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(of = "name")
public class User {
    private String name;
}
