package com.satvik.sysd.parkinglot.entity;


import com.satvik.sysd.parkinglot.model.VehicleTypeEnum;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Vehicle {
    private String numberPlate;
    private VehicleTypeEnum vehicleType;
}
