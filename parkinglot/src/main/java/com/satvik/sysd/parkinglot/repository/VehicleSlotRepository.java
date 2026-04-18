package com.satvik.sysd.parkinglot.repository;

import com.satvik.sysd.parkinglot.entity.Slot;
import com.satvik.sysd.parkinglot.entity.Vehicle;
import com.satvik.sysd.parkinglot.entity.VehicleSlot;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class VehicleSlotRepository implements Repository<VehicleSlot, String> {
    private final ConcurrentHashMap<String, VehicleSlot> data;

    public VehicleSlotRepository() {
        this.data = new ConcurrentHashMap<>();
    }

    @Override
    public VehicleSlot save(VehicleSlot vehicleSlot) {
        data.put(vehicleSlot.getId(), vehicleSlot);
        return data.get(vehicleSlot.getId());
    }

    @Override
    public VehicleSlot findById(String s) {
        return data.get(s);
    }

    @Override
    public VehicleSlot removeById(String s) {
        return data.remove(s);
    }


    public Optional<VehicleSlot> vehicleIsParked(Vehicle v) {
        String id = v.getNumberPlate();
        Optional<VehicleSlot> result = Optional.empty();
        for(String key: data.keySet()){
            if(key.split(" ")[0].equals(id)){
                result = Optional.of(data.get(key));
                break;
            }
        }
        return result;
    }
}
