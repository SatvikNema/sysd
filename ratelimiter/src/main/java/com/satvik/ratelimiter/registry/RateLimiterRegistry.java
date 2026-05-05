package com.satvik.ratelimiter.registry;

import com.satvik.ratelimiter.factory.RateLimiterFactory;
import com.satvik.ratelimiter.model.RateLimiterAlgorithm;
import com.satvik.ratelimiter.model.RateLimiterConfig;
import com.satvik.ratelimiter.model.RateLimiterResponse;
import com.satvik.ratelimiter.model.Request;
import com.satvik.ratelimiter.service.RateLimiter;
import com.satvik.ratelimiter.service.TokenBucketRateLimiter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RateLimiterRegistry {
    private final Map<String, RateLimiter> rateLimiterMap;
    private static final RateLimiter DEFAULT_RATE_LIMITER = new TokenBucketRateLimiter(100, 20);

    public RateLimiterRegistry(){
        rateLimiterMap = new ConcurrentHashMap<>();
    }

    public void registerLimiter(RateLimiterConfig config){
        String endpoint = config.getEndpoint();
        RateLimiter rateLimiter = RateLimiterFactory.createRateLimiter(config);
        RateLimiter value = rateLimiterMap.putIfAbsent(endpoint, rateLimiter);
        if(value != null){
            throw new IllegalStateException("Rate limiter already exists for endpoint: " + endpoint);
        }
    }

    public RateLimiterResponse routeRequest(Request request){
        String endPoint = request.getEndpoint();
        RateLimiter rateLimiter = rateLimiterMap.getOrDefault(endPoint, DEFAULT_RATE_LIMITER);
        return rateLimiter.check(request);
    }
}
