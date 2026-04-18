package com.satvik.sysd.parkinglot.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VehicleSlot {
    private String id;
    private long inEpochTime;
    private long outEpochTime;
    private double price;
}
