package com.satvik.sysd.parkinglot.repository;

import com.satvik.sysd.parkinglot.entity.Slot;
import com.satvik.sysd.parkinglot.entity.Vehicle;
import com.satvik.sysd.parkinglot.model.VehicleTypeEnum;

import javax.swing.text.html.Option;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SlotRepository implements Repository<Slot, String> {

    private final ConcurrentHashMap<String, Slot> data;

    public SlotRepository() {
        this.data = new ConcurrentHashMap<>();
    }

    @Override
    public void init() {
        for(int i=0;i<50;i++){
            String id = "slot_"+i;
            save(Slot.builder().id(id).vehicleType(VehicleTypeEnum.TWO_WHEELER).floor(i%4).occupied(false).build());
        }
        for(int i=50;i<100;i++){
            String id = "slot_"+i;
            save(Slot.builder().id(id).vehicleType(VehicleTypeEnum.FOUR_WHEELER).floor(i%4).occupied(false).build());
        }
    }

    @Override
    public Slot removeById(String s) {
        return data.remove(s);
    }

    @Override
    public Slot save(Slot slot) {
        data.put(slot.getId(), slot);
        return data.get(slot.getId());
    }

    @Override
    public Slot findById(String s) {
        return data.get(s);
    }

    public Optional<Slot> findFirstEmpty(Vehicle vehicle){
        Optional<Slot> result = Optional.empty();
        for(Map.Entry<String, Slot> entry : data.entrySet()){
            Slot slot = entry.getValue();
            if(slot.getVehicleType() == vehicle.getVehicleType() && !slot.isOccupied()){
                result = Optional.of(slot);
                break;
            }
        }
        return result;
    }
}
