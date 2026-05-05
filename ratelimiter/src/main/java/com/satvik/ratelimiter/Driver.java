package com.satvik.ratelimiter;

import com.satvik.ratelimiter.model.AlgoConfig;
import com.satvik.ratelimiter.model.RateLimiterAlgorithm;
import com.satvik.ratelimiter.model.RateLimiterConfig;
import com.satvik.ratelimiter.model.RateLimiterResponse;
import com.satvik.ratelimiter.model.Request;
import com.satvik.ratelimiter.registry.RateLimiterRegistry;

import java.util.Scanner;

public class Driver {
    public void start(){
        RateLimiterRegistry registry = new RateLimiterRegistry();
        RateLimiterConfig rateLimiterConfig = RateLimiterConfig.builder()
                .algoConfig(AlgoConfig
                        .builder()
                        .capacity(6)
                        .refillRatePerSecond(2)
                        .build())
                .endpoint("/wat")
                .rateLimiterAlgorithm(RateLimiterAlgorithm.TOKEN_BUCKET)
                .build();

        registry.registerLimiter(rateLimiterConfig);

        Request request1 = Request.builder().endpoint("/wat").clientId("1").build();
        Request request2 = Request.builder().endpoint("/wat").clientId("2").build();

        Scanner sc = new Scanner(System.in);
        RateLimiterResponse response = null;
        while (true) {
            System.out.print("Enter command (type 'exit' to quit):\n");
            String input = sc.nextLine();

            if (input.equalsIgnoreCase("exit")) {
                break;
            }

            if("1".equals(input)){
                 response = registry.routeRequest(request1);
            } else if("2".equals(input)){
                response = registry.routeRequest(request2);
            }
            System.out.println(response);
        }
    }
}
