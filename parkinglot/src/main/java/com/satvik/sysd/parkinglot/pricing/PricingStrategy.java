package com.satvik.sysd.parkinglot.pricing;

import com.satvik.sysd.parkinglot.entity.VehicleSlot;

public interface PricingStrategy {
    double calculate(VehicleSlot vehicleSlot);
}
