package com.satvik.sysd.parkinglot.repository;

import com.satvik.sysd.parkinglot.entity.Slot;
import com.satvik.sysd.parkinglot.entity.Vehicle;
import com.satvik.sysd.parkinglot.model.VehicleTypeEnum;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class VehicleRepository implements Repository<Vehicle, String>{
    private final ConcurrentHashMap<String, Vehicle> data;

    public VehicleRepository() {
        this.data = new ConcurrentHashMap<>();
    }

    @Override
    public void init() {
        for(int i=0;i<50;i++){

            String id = "vehicle_"+i;
            save(Vehicle.builder().numberPlate(id).vehicleType(VehicleTypeEnum.FOUR_WHEELER).build());
        }
        for(int i=50;i<100;i++){
            String id = "vehicle_"+i;
            save(Vehicle.builder().numberPlate(id).vehicleType(VehicleTypeEnum.TWO_WHEELER).build());
        }
    }

    @Override
    public Vehicle save(Vehicle vehicle) {
        data.put(vehicle.getNumberPlate(), vehicle);
        return data.get(vehicle.getNumberPlate());
    }

    @Override
    public Vehicle findById(String s) {
        return data.get(s);
    }

    @Override
    public Vehicle removeById(String s) {
        return data.remove(s);
    }
}
