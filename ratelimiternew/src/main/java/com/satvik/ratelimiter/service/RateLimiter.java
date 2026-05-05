package com.satvik.ratelimiter.service;

import com.satvik.ratelimiter.model.RateLimiterResponse;
import com.satvik.ratelimiter.model.Request;

public interface RateLimiter {
    RateLimiterResponse check(Request request);
}
