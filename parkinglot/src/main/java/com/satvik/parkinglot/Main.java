package com.satvik.parkinglot;

import com.satvik.parkinglot.allocator.FirstAvailableAllocator;
import com.satvik.parkinglot.allocator.SlotAllocator;
import com.satvik.parkinglot.exception.NoSlotAvailableException;
import com.satvik.parkinglot.exception.VehicleAlreadyParkedException;
import com.satvik.parkinglot.model.Slot;
import com.satvik.parkinglot.model.Ticket;
import com.satvik.parkinglot.model.Vehicle;
import com.satvik.parkinglot.model.VehicleType;
import com.satvik.parkinglot.pricing.FlatHourlyPricingStrategy;
import com.satvik.parkinglot.pricing.PricingStrategy;
import com.satvik.parkinglot.service.ParkingLot;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.LongSupplier;

public class Main {

    public static void main(String[] args) {
        List<Slot> slots = seedSlots(2, 2); // 2 two-wheeler + 2 four-wheeler

        SlotAllocator allocator = new FirstAvailableAllocator(slots);

        Map<VehicleType, Double> rates = new EnumMap<>(VehicleType.class);
        rates.put(VehicleType.TWO_WHEELER, 10.0);
        rates.put(VehicleType.FOUR_WHEELER, 20.0);
        PricingStrategy pricing = new FlatHourlyPricingStrategy(rates);

        long[] simulatedNow = {1_700_000_000L};
        LongSupplier clock = () -> simulatedNow[0];

        ParkingLot lot = new ParkingLot(allocator, pricing, clock);

        Vehicle bike = new Vehicle("KA-01-1111", VehicleType.TWO_WHEELER);
        Vehicle car1 = new Vehicle("KA-01-AAAA", VehicleType.FOUR_WHEELER);
        Vehicle car2 = new Vehicle("KA-01-BBBB", VehicleType.FOUR_WHEELER);
        Vehicle car3 = new Vehicle("KA-01-CCCC", VehicleType.FOUR_WHEELER);

        Ticket t1 = lot.park(bike);
        Ticket t2 = lot.park(car1);
        Ticket t3 = lot.park(car2);
        System.out.println("parked: " + t1.getId() + ", " + t2.getId() + ", " + t3.getId());

        try {
            lot.park(car1);
        } catch (VehicleAlreadyParkedException e) {
            System.out.println("expected duplicate: " + e.getMessage());
        }

        try {
            lot.park(car3);
        } catch (NoSlotAvailableException e) {
            System.out.println("expected no-slot: " + e.getMessage());
        }

        simulatedNow[0] += 2 * 3600 + 1; // 2h + 1s → billed as 3 hours (ceil)

        Ticket done = lot.unpark(t2.getId());
        System.out.println("unparked " + done.getVehicle().numberPlate()
                + " from " + done.getSlot().id()
                + ", charged " + done.getAmountCharged());

        Ticket t4 = lot.park(car3);
        System.out.println("reparked car3 into freed slot: " + t4.getSlot().id());

        Ticket done2 = lot.unpark(t1.getId());
        System.out.println("unparked " + done2.getVehicle().numberPlate()
                + " from " + done2.getSlot().id()
                + ", charged " + done2.getAmountCharged());
    }

    private static List<Slot> seedSlots(int twoWheeler, int fourWheeler) {
        List<Slot> slots = new ArrayList<>();
        for (int i = 0; i < twoWheeler; i++) {
            slots.add(new Slot("TW-" + i, VehicleType.TWO_WHEELER, 0));
        }
        for (int i = 0; i < fourWheeler; i++) {
            slots.add(new Slot("FW-" + i, VehicleType.FOUR_WHEELER, 1));
        }
        return slots;
    }
}
