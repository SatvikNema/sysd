package com.satvik.ratelimiter.service;

import com.satvik.ratelimiter.model.Bucket;
import com.satvik.ratelimiter.model.RateLimiterResponse;
import com.satvik.ratelimiter.model.Request;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class TokenBucketRateLimiter implements RateLimiter {

    private final long capacity;
    private final int refillRatePerSecond;

    private final Map<String, Bucket> clientBuckets;

    public TokenBucketRateLimiter(long capacity, int refillRatePerSecond) {
        // Initialize token bucket parameters based on config
        this.capacity = capacity;
        this.refillRatePerSecond = refillRatePerSecond;
        this.clientBuckets = new ConcurrentHashMap<>();
    }

    @Override
    public RateLimiterResponse check(Request request) {
        String client = request.getClientId();


        Bucket bucket = clientBuckets.computeIfAbsent(client, k -> Bucket
                .builder()
                .tokens(capacity)
                .lastRefillNano(System.nanoTime())
                .build());
        RateLimiterResponse response;
        synchronized (bucket){
            long currentNano = System.nanoTime();
            long lastRefillNano = bucket.getLastRefillNano();
            long elapsedNano = currentNano - lastRefillNano;
            long refillTokens = elapsedNano * refillRatePerSecond / 1_000_000_000;

            if(refillTokens > 0){
                long newTokens = Math.min(capacity, bucket.getTokens()+refillTokens) ;
                bucket.setTokens(newTokens);
                bucket.setLastRefillNano(currentNano);

                // more tighter last refill should correspond to the nanos of tokens worth added
                // that is:
                // long nanosPerToken = 1_000_000_000 / refillRatePerSecond;
                // bucket.setLastRefillNano(bucket.getLastRefillNano() + (nanosPerToken * refillTokens));
            }

            if(bucket.getTokens() > 0){
                bucket.setTokens(bucket.getTokens() - 1);
                response = RateLimiterResponse
                        .builder()
                        .allowed(true)
                        .remaining(bucket.getTokens())
                        .build();
            } else {
                // this is a tighter calculation than below
                long nanosPerToken = 1_000_000_000 / refillRatePerSecond;
                long nextTokenAtNano = bucket.getLastRefillNano() + nanosPerToken;
                long retryAfterNano = Math.max(0, nextTokenAtNano - currentNano);
                long retryAfterMs =  retryAfterNano / 1_000_000;

                // looser and more pessimist calculation is
                // long retryAfter = 1_000 / refillRatePerSecond;
                response = RateLimiterResponse
                        .builder()
                        .allowed(false)
                        .remaining(bucket.getTokens())
                        .retryAfterMs(retryAfterMs)
                        .build();
            }
        }
        return response;
    }

}
