package com.satvik.ratelimiter.factory;

import com.satvik.ratelimiter.model.RateLimiterAlgorithm;
import com.satvik.ratelimiter.model.RateLimiterConfig;
import com.satvik.ratelimiter.service.RateLimiter;
import com.satvik.ratelimiter.service.TokenBucketRateLimiter;

public class RateLimiterFactory {
    public static RateLimiter createRateLimiter(RateLimiterConfig config) {
        RateLimiterAlgorithm algo = config.getRateLimiterAlgorithm();
        RateLimiter rateLimiter;
        switch (algo) {
            case TOKEN_BUCKET ->
                    rateLimiter = new TokenBucketRateLimiter(config.getAlgoConfig().getCapacity(), config.getAlgoConfig().getRefillRatePerSecond());
            default -> throw new IllegalStateException("Unsupported algorithm: " + algo);
        }
        return rateLimiter;
    }
}
