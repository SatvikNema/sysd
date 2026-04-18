package com.satvik.sysd.parkinglot.pricing;

public class PricingStrategyResolver {
    public static PricingStrategy get(PricingStrategyEnum strategyEnum){
        if(strategyEnum == PricingStrategyEnum.FLAT_HOURLY){
            return new FlatHourlyPricingStrategy();
        } else {
            throw new IllegalArgumentException("this pricing strategy does not exist");
        }
    }
}
