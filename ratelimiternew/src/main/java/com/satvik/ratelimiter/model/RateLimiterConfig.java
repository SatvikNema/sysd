package com.satvik.ratelimiter.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RateLimiterConfig {
    private String endpoint;
    private RateLimiterAlgorithm rateLimiterAlgorithm;
    private AlgoConfig algoConfig;
}
