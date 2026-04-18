package com.satvik.sysd.ratelimiter;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RequestsTracker {

    private final Map<String, TreeMap<Long, Integer>> requestsCounter;
    public RequestsTracker() {
        requestsCounter = new ConcurrentHashMap<>();
    }

    public void putRequest(String key) {
        TreeMap<Long, Integer> requestCounts = requestsCounter.getOrDefault(key, new TreeMap<>());

        long currentTime = Instant.now().getEpochSecond();
        int count = requestCounts.getOrDefault(currentTime, 0);
        requestCounts.put(currentTime, count + 1);
        requestsCounter.put(key, requestCounts);
    }

    public TreeMap<Long, Integer> getUserRequestHistory(String key){
        return requestsCounter.getOrDefault(key, new TreeMap<>());
    }

    public void printData(){
        System.out.println(requestsCounter);
    }
}
