package org.satvik.practise;

public class Driver {
    static void main() {
        Driver d = new Driver();

    }
}

/*
Entities
vehicle -> numberPlate, type
slot -> type, floor, occupied, location...
ticket -> vehicle, slot, startTime, endTime


Service
ISlotAssigner ->
    1. assign(vehicle) -> Slot
    2. release(ticket) -> Slot
    FirstFreeSlotAssigner

ParkingLot
    1. park(vehicle)
    2. unpark(vehicle)


PricingStrategy
    1. FlatPricingStrategy

 */
