package com.satvik.parkinglot.allocator;

import com.satvik.parkinglot.exception.NoSlotAvailableException;
import com.satvik.parkinglot.model.Slot;
import com.satvik.parkinglot.model.VehicleType;

public interface SlotAllocator {

    Slot allocate(VehicleType type) throws NoSlotAvailableException;

    void release(Slot slot);
}
