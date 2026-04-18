package com.satvik.sysd.ratelimiter;

import com.satvik.sysd.exception.RateLimitException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.NavigableMap;
import java.util.TreeMap;

@Component
public class SlidingWindowCounterRateLimiter {

    public static final int REQUESTS_PER_MINUTE = 3;

    private final RequestsTracker requestsTracker;

    public SlidingWindowCounterRateLimiter(RequestsTracker requestsTracker) {
        this.requestsTracker = requestsTracker;
    }

    private String extractKey(HttpServletRequest request) {
        return request.getRemoteAddr();
    }

    private void takeMeasure(String key){
        requestsTracker.putRequest(key);
    }

    private boolean currentRequestExceedsLimit(HttpServletRequest request){
        String key = extractKey(request);
        takeMeasure(key);
        TreeMap<Long, Integer> treeMap = requestsTracker.getUserRequestHistory(key);
        long currentTimeInSeconds = Instant.now().getEpochSecond();

        Long cielKey = treeMap.floorKey(currentTimeInSeconds);
        long takeAfter = currentTimeInSeconds - 60;

        Long floorKey = treeMap.ceilingKey(takeAfter);
        if(floorKey == null && cielKey == null){
            return false;
        }
        if(floorKey == null){
            floorKey = cielKey;
        } else if(cielKey == null){
            cielKey = floorKey;
        }

        NavigableMap<Long, Integer> mapOfInterest = treeMap.subMap(floorKey, true, cielKey, true);

        long userRequests = 0;
        if(mapOfInterest != null){
            userRequests = mapOfInterest.values().stream().reduce(0, Integer::sum);
        }

        return userRequests > REQUESTS_PER_MINUTE;

    }

    public void checkRateLimit(HttpServletRequest request){
        boolean requestExceedsLimit = currentRequestExceedsLimit(request);
        if(requestExceedsLimit){
            throw new RateLimitException("Request exceeds limit of "+REQUESTS_PER_MINUTE+" per minute");
        }
    }

}
