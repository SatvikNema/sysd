package com.satvik.sysd.parkinglot.entity;

import com.satvik.sysd.parkinglot.model.VehicleTypeEnum;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Slot {
    private String id;
    private VehicleTypeEnum vehicleType;
    private int floor;
    private boolean occupied;
}
