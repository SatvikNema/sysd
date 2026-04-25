package com.satvik.parkinglot.allocator;

import com.satvik.parkinglot.exception.NoSlotAvailableException;
import com.satvik.parkinglot.model.Slot;
import com.satvik.parkinglot.model.VehicleType;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class FirstAvailableAllocator implements SlotAllocator {

    private final Map<VehicleType, Deque<Slot>> freeSlotsByType;
    private final Map<VehicleType, ReentrantLock> locksByType;

    public FirstAvailableAllocator(List<Slot> slots) {
        this.freeSlotsByType = new EnumMap<>(VehicleType.class);
        this.locksByType = new EnumMap<>(VehicleType.class);
        for (VehicleType type : VehicleType.values()) {
            freeSlotsByType.put(type, new ArrayDeque<>());
            locksByType.put(type, new ReentrantLock());
        }
        for (Slot slot : slots) {
            freeSlotsByType.get(slot.type()).offer(slot);
        }
    }

    @Override
    public Slot allocate(VehicleType type) {
        ReentrantLock lock = locksByType.get(type);
        lock.lock();
        try {
            Slot slot = freeSlotsByType.get(type).poll();
            if (slot == null) {
                throw new NoSlotAvailableException("No slot available for " + type);
            }
            return slot;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void release(Slot slot) {
        ReentrantLock lock = locksByType.get(slot.type());
        lock.lock();
        try {
            freeSlotsByType.get(slot.type()).offer(slot);
        } finally {
            lock.unlock();
        }
    }
}
