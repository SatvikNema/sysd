package com.satvik.parkinglot.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class Ticket {
    private final String id;
    private final Vehicle vehicle;
    private final Slot slot;
    private final long entryEpoch;

    @Setter private long exitEpoch;
    @Setter private double amountCharged;
}
