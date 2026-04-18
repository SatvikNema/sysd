package com.satvik.sysd.parkinglot.service;

import com.satvik.sysd.parkinglot.entity.VehicleSlot;
import com.satvik.sysd.parkinglot.pricing.PricingStrategy;
import com.satvik.sysd.parkinglot.pricing.PricingStrategyEnum;
import com.satvik.sysd.parkinglot.pricing.PricingStrategyResolver;

public class PriceCalculator {

    private final PricingStrategy pricingStrategy;

    public PriceCalculator(PricingStrategyEnum pricingStrategyEnum){
        this.pricingStrategy = PricingStrategyResolver.get(pricingStrategyEnum);
    }

    public double calculate(VehicleSlot vehicleSlot){
        return pricingStrategy.calculate(vehicleSlot);
    }
}
