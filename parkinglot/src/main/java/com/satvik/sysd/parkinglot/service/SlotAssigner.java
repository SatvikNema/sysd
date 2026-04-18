package com.satvik.sysd.parkinglot.service;

import com.satvik.sysd.parkinglot.entity.Slot;
import com.satvik.sysd.parkinglot.entity.Vehicle;
import com.satvik.sysd.parkinglot.entity.VehicleSlot;
import com.satvik.sysd.parkinglot.repository.SlotRepository;
import com.satvik.sysd.parkinglot.repository.VehicleSlotRepository;

import java.time.Instant;
import java.util.Optional;

public class SlotAssigner {

    private final SlotRepository slotRepository;
    private final VehicleSlotRepository vehicleSlotRepository;

    public SlotAssigner(SlotRepository slotRepository, VehicleSlotRepository vehicleSlotRepository){
        this.slotRepository = slotRepository;
        this.vehicleSlotRepository = vehicleSlotRepository;
    }

    public Optional<VehicleSlot> assign(Vehicle vehicle){
        Optional<VehicleSlot> result = verifyVehicleIsNotAlreadyParked(vehicle);
        if(result.isPresent()){
            System.out.println("Vehicle " + vehicle.getNumberPlate() + " is already parked in " + result.get().getId() + " currently");
            return result;
        }

        Optional<Slot> optionalSlot = slotRepository.findFirstEmpty(vehicle);
        if(optionalSlot.isPresent()){
            Slot slot = optionalSlot.get();
            VehicleSlot vehicleSlot = VehicleSlot
                    .builder()
                    .id(vehicle.getNumberPlate()+ " "+slot.getId())
                    .inEpochTime(Instant.now().getEpochSecond())
                    .build();
            slot.setOccupied(true);
            vehicleSlotRepository.save(vehicleSlot);
            result = Optional.of(vehicleSlot);
            System.out.println("assigning "+vehicle.getNumberPlate()+"("+vehicle.getVehicleType()+") to slot "+slot.getId()+" - "+slot.getVehicleType());
        } else {
            System.out.println("could not assign "+vehicle.getNumberPlate()+" to any slot");
        }
        return result;
    }

    public VehicleSlot vacate(VehicleSlot vehicleSlot){
        String[] vehicleSlotId = vehicleSlot.getId().split(" ");
        String slotId = vehicleSlotId[1];
        Slot slot = slotRepository.findById(slotId);
        if(slot == null) throw new RuntimeException("slot with id "+slotId+" not found");
        slot.setOccupied(false);
        vehicleSlot.setOutEpochTime(Instant.now().getEpochSecond()+7200);
        return vehicleSlotRepository.removeById(vehicleSlot.getId());
    }

    private Optional<VehicleSlot> verifyVehicleIsNotAlreadyParked(Vehicle vehicle) {
        Optional<VehicleSlot> result = Optional.empty();
        Optional<VehicleSlot> vehicleSlotOptional = vehicleSlotRepository.vehicleIsParked(vehicle);

        if(vehicleSlotOptional.isPresent()){
            VehicleSlot vehicleSlot = vehicleSlotOptional.get();
            String occupiedSlotId = vehicleSlot.getId().split(" ")[1];
            Slot slot = slotRepository.findById(occupiedSlotId);
            if(slot == null){
                throw new RuntimeException("slot "+occupiedSlotId+" does not exist");
            }
            if(slot.isOccupied()){
                result = Optional.of(vehicleSlot);
            }
        }
        return result;
    }
}
