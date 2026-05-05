package com.satvik.ratelimiter.service;

import com.satvik.ratelimiter.model.RateLimiterResponse;
import com.satvik.ratelimiter.model.Request;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class TokenBucketRateLimiter implements RateLimiter {

    private final ScheduledExecutorService scheduler;
    private final long capacity;
    private final int refillRatePerSecond;

    private final Map<String, AtomicLong> clientTokens;

    public TokenBucketRateLimiter(long capacity, int refillRatePerSecond) {
        // Initialize token bucket parameters based on config
        this.capacity = capacity;
        this.refillRatePerSecond = refillRatePerSecond;
        this.clientTokens = new ConcurrentHashMap<>();

        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::fillBucket,
                0,
                1,
                TimeUnit.SECONDS);
    }

    @Override
    public RateLimiterResponse check(Request request) {
        String client = request.getClientId();
        AtomicLong tokens = clientTokens.computeIfAbsent(client, k -> new AtomicLong(capacity));
        RateLimiterResponse result;
        while(true) {
            long currentTokens = tokens.get();
            if (currentTokens > 0) {
                if (tokens.compareAndSet(currentTokens, currentTokens - 1)) {
                    result = RateLimiterResponse
                            .builder()
                            .allowed(true)
                            .remaining(currentTokens - 1)
                            .build();
                    break;
                }
            } else {
                result = RateLimiterResponse
                        .builder()
                        .allowed(false)
                        .retryAfterMs(1000L)
                        .build();
                break;
            }
        }
        return result;
    }

    public void fillBucket(){
        clientTokens.forEach((key, tokens) -> {
            while (true) {
                long currentCapacity = tokens.get();
                long newValue = Math.min(capacity, currentCapacity + refillRatePerSecond);
                if (tokens.compareAndSet(currentCapacity, newValue)) {
                    break;
                }
            }
        });
    }
}
