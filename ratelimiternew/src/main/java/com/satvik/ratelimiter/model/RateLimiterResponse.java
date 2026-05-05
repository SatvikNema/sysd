package com.satvik.ratelimiter.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RateLimiterResponse {
    private boolean allowed;
    private long remaining;
    private Long retryAfterMs;
}
