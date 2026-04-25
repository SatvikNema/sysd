package com.satvik.parkinglot.pricing;

import com.satvik.parkinglot.model.Ticket;
import com.satvik.parkinglot.model.VehicleType;

import java.util.EnumMap;
import java.util.Map;

public class FlatHourlyPricingStrategy implements PricingStrategy {

    private static final int SECONDS_PER_HOUR = 3600;

    private final Map<VehicleType, Double> hourlyRates;

    public FlatHourlyPricingStrategy(Map<VehicleType, Double> hourlyRates) {
        this.hourlyRates = new EnumMap<>(hourlyRates);
    }

    @Override
    public double calculate(Ticket ticket) {
        long durationSeconds = Math.max(0, ticket.getExitEpoch() - ticket.getEntryEpoch());
        long hours = (long) Math.ceil((double) durationSeconds / SECONDS_PER_HOUR);
        double rate = hourlyRates.get(ticket.getVehicle().type());
        return hours * rate;
    }
}
