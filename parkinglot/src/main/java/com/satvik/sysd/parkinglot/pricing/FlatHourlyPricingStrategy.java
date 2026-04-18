package com.satvik.sysd.parkinglot.pricing;

import com.satvik.sysd.parkinglot.entity.VehicleSlot;

public class FlatHourlyPricingStrategy implements PricingStrategy{

    private static final int EPOCHS_IN_ONE_HOUR = 60 * 60;
    private static final int FLAT_HOURLY_RATE = 5;

    @Override
    public double calculate(VehicleSlot vehicleSlot) {
        double hours = (double) (vehicleSlot.getOutEpochTime() - vehicleSlot.getInEpochTime()) / EPOCHS_IN_ONE_HOUR;
        return hours * FLAT_HOURLY_RATE;
    }
}
