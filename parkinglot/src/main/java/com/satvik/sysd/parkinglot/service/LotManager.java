package com.satvik.sysd.parkinglot.service;

import com.satvik.sysd.parkinglot.entity.Slot;
import com.satvik.sysd.parkinglot.entity.Vehicle;
import com.satvik.sysd.parkinglot.entity.VehicleSlot;
import com.satvik.sysd.parkinglot.pricing.PricingStrategy;
import com.satvik.sysd.parkinglot.repository.VehicleSlotRepository;

import java.time.Instant;
import java.util.Optional;

public class LotManager {
    private final SlotAssigner slotAssigner;
    private final PriceCalculator priceCalculator;

    public LotManager(SlotAssigner slotAssigner, PriceCalculator priceCalculator){
        this.priceCalculator = priceCalculator;
        this.slotAssigner = slotAssigner;
    }

    public Optional<VehicleSlot> assign(Vehicle vehicle){
        return slotAssigner.assign(vehicle);
    }

    public VehicleSlot vacate(VehicleSlot vehicleSlot){
        VehicleSlot vacatedSlot = slotAssigner.vacate(vehicleSlot);
        double amount = priceCalculator.calculate(vehicleSlot);
        System.out.println("Vacated "+vacatedSlot.getId()+". Amount to collect: "+amount);
        return vacatedSlot;
    }
}
