package com.satvik.parkinglot.service;

import com.satvik.parkinglot.allocator.SlotAllocator;
import com.satvik.parkinglot.exception.TicketNotFoundException;
import com.satvik.parkinglot.exception.VehicleAlreadyParkedException;
import com.satvik.parkinglot.model.Slot;
import com.satvik.parkinglot.model.Ticket;
import com.satvik.parkinglot.model.Vehicle;
import com.satvik.parkinglot.pricing.PricingStrategy;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.LongSupplier;

public class ParkingLot {

    private final SlotAllocator allocator;
    private final PricingStrategy pricingStrategy;
    private final LongSupplier clock;

    private final Map<String, Ticket> activeTickets = new ConcurrentHashMap<>();
    private final Map<String, String> vehicleToTicketId = new ConcurrentHashMap<>();

    public ParkingLot(SlotAllocator allocator, PricingStrategy pricingStrategy, LongSupplier clock) {
        this.allocator = allocator;
        this.pricingStrategy = pricingStrategy;
        this.clock = clock;
    }

    public synchronized Ticket park(Vehicle vehicle) {
        if (vehicleToTicketId.containsKey(vehicle.numberPlate())) {
            throw new VehicleAlreadyParkedException(
                    "Vehicle " + vehicle.numberPlate() + " is already parked");
        }

        Slot slot = allocator.allocate(vehicle.type());

        Ticket ticket = Ticket.builder()
                .id(UUID.randomUUID().toString())
                .vehicle(vehicle)
                .slot(slot)
                .entryEpoch(clock.getAsLong())
                .build();

        activeTickets.put(ticket.getId(), ticket);
        vehicleToTicketId.put(vehicle.numberPlate(), ticket.getId());

        return ticket;
    }

    public synchronized Ticket unpark(String ticketId) {
        Ticket ticket = activeTickets.remove(ticketId);
        if (ticket == null) {
            throw new TicketNotFoundException("Ticket " + ticketId + " not found");
        }
        vehicleToTicketId.remove(ticket.getVehicle().numberPlate());

        ticket.setExitEpoch(clock.getAsLong());
        ticket.setAmountCharged(pricingStrategy.calculate(ticket));

        allocator.release(ticket.getSlot());
        return ticket;
    }
}
