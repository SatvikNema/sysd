package com.satvik.sysd.parkinglot;

import com.satvik.sysd.parkinglot.entity.Vehicle;
import com.satvik.sysd.parkinglot.entity.VehicleSlot;
import com.satvik.sysd.parkinglot.pricing.PricingStrategyEnum;
import com.satvik.sysd.parkinglot.repository.Repository;
import com.satvik.sysd.parkinglot.repository.SlotRepository;
import com.satvik.sysd.parkinglot.repository.VehicleRepository;
import com.satvik.sysd.parkinglot.repository.VehicleSlotRepository;
import com.satvik.sysd.parkinglot.service.LotManager;
import com.satvik.sysd.parkinglot.service.PriceCalculator;
import com.satvik.sysd.parkinglot.service.SlotAssigner;

import java.util.List;
import java.util.Optional;

public class Main {
    public static void main(String[] args) {
        SlotRepository slotRepository = new SlotRepository();
        VehicleRepository vehicleRepository = new VehicleRepository();
        VehicleSlotRepository vehicleSlotRepository = new VehicleSlotRepository();

        List<Repository> repositories = List.of(
                slotRepository, vehicleRepository, vehicleSlotRepository
        );

        repositories.forEach(Repository::init);

        SlotAssigner slotAssigner = new SlotAssigner(slotRepository, vehicleSlotRepository);
        PriceCalculator priceCalculator = new PriceCalculator(PricingStrategyEnum.FLAT_HOURLY);

        LotManager lotManager = new LotManager(slotAssigner, priceCalculator);
        Vehicle vehicle25 = vehicleRepository.findById("vehicle_25");
        Vehicle vehicle53 = vehicleRepository.findById("vehicle_53");
        Optional<VehicleSlot> r1 = lotManager.assign(vehicle25);
        Optional<VehicleSlot> r2 = lotManager.assign(vehicle53);

        lotManager.vacate(r1.get());
        lotManager.vacate(r2.get());
    }
}


/**
 entity
 VehicleTypeEnum - TWO_WHEELER, FOUR_WHEELER
 Vehicle - numberPlate, type, slot
 Slot - id, type, floor, occupied, vehicleId
 VehicleSlot - slotId, vehicleId, intTime, outTime

 times are absolute epoch

 interface PricingStrategy
    calculate(vehicle, inTime, outTime)


 service
 SlotAssigner
    assign(vehicle) -> VehicleSlot (slot assigned, null if no slot assigned)
    vacate(vehicle) -> VehicleSlot (slot which was vacated, null if slot already empty)
 PriceCalculator - pricing strategy pattern using interface PricingStrategy

 driver
    instantiates slots, pricing strategy
    simulates incoming/outgoing vehicles and assigns/vacates slots
 */