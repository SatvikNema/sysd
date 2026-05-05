package com.satvik.ratelimiter.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AlgoConfig {
    private long capacity;
    private int refillRatePerSecond;
    private int requestsPerSecond;
}
