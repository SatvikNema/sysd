package com.satvik.parkinglot.allocator;

import com.satvik.parkinglot.exception.NoSlotAvailableException;
import com.satvik.parkinglot.model.Slot;
import com.satvik.parkinglot.model.VehicleType;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class FirstAvailableAllocator implements SlotAllocator {

    private final Map<VehicleType, Deque<Slot>> freeSlotsByType;

    public FirstAvailableAllocator(List<Slot> slots) {
        this.freeSlotsByType = new EnumMap<>(VehicleType.class);
        for (VehicleType type : VehicleType.values()) {
            freeSlotsByType.put(type, new ArrayDeque<>());
        }
        for (Slot slot : slots) {
            freeSlotsByType.get(slot.type()).offer(slot);
        }
    }

    @Override
    public synchronized Slot allocate(VehicleType type) {
        Slot slot = freeSlotsByType.get(type).poll();
        if (slot == null) {
            throw new NoSlotAvailableException("No slot available for " + type);
        }
        return slot;
    }

    @Override
    public synchronized void release(Slot slot) {
        freeSlotsByType.get(slot.type()).offer(slot);
    }
}
