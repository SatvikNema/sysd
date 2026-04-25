package com.satvik.parkinglot.pricing;

import com.satvik.parkinglot.model.Ticket;

public interface PricingStrategy {
    double calculate(Ticket ticket);
}
