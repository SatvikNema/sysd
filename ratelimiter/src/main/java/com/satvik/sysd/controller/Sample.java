package com.satvik.sysd.controller;

import com.satvik.sysd.ratelimiter.RequestsTracker;
import com.satvik.sysd.ratelimiter.SlidingWindowCounterRateLimiter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class Sample {
    private final SlidingWindowCounterRateLimiter slidingWindowCounterRateLimiter;

    public Sample(RequestsTracker requestsTracker, SlidingWindowCounterRateLimiter slidingWindowCounterRateLimiter) {
        this.slidingWindowCounterRateLimiter = slidingWindowCounterRateLimiter;
    }

    @GetMapping(path = "/home")
    public Map<String, String> home(HttpServletRequest request) {
        slidingWindowCounterRateLimiter.checkRateLimit(request);
        StringBuilder sb = new StringBuilder(request.getRemoteAddr());
        sb.append(" -- ").append(request.getRemoteHost());
        sb.append(" -- ").append(request.getRemotePort());
        sb.append(" -- ").append(request.getRemoteUser());
        return Map.of("location", sb.toString());
    }
}
