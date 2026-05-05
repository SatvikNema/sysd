package com.satvik.ratelimiter;

public class Main {
    public static void main(String[] args) {
        Driver driver = new Driver();
        driver.start();
    }
}

/*
Requirements:
1. Configuration is provided at startup (loaded once)
2. System receives requests with (clientId: string, endpoint: string)
3. Each endpoint has a configuration specifying:
   - Algorithm to use (e.g., "TokenBucket", "SlidingWindowLog", etc.)
   - Algorithm-specific parameters (e.g., capacity, refillRatePerSecond for Token Bucket)
4. System enforces rate limits by checking clientId against the endpoint's configuration
5. Return structured result: (allowed: boolean, remaining: int, retryAfterMs: long | null)
6. If endpoint has no configuration, use a default limit

Out of scope:
- Distributed rate limiting (Redis, coordination)
- Dynamic configuration updates
- Metrics and monitoring
- Config validation beyond basic checks

=== Solution ===
Entities:
 - AlgoConfig - capacity, refillRatePerSecond, requestsPerSecond
 - RateLimiterConfig - algorithm, endpoint, algoConfig
 - Request - clientId, endpoint
 - RateLimiterResponse - allowed, remaining, retryAfterMs

Service layer
 - RateLimiter interface -> check(Request) returns RateLimiterResponse
   - TokenBucket
   - FixedWindow
   - SlidingWindow

For each RateLimiterConfig, a new RateLimiter will be setup.
The incoming request endpoints will be routed through those rate limiters.
Each endpoint's ratelimiter will be a singleton

 */