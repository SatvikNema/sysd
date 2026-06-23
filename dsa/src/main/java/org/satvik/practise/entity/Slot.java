package org.satvik.practise.entity;

import java.util.Objects;

public class Slot {
    private String id;
    private VehicleType vehicleType;
    private int floor;
    private boolean occupied;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Slot slot = (Slot) o;
        return floor == slot.floor && occupied == slot.occupied && Objects.equals(id, slot.id) && vehicleType == slot.vehicleType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, vehicleType, floor, occupied);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }
}
