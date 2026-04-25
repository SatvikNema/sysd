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

    public Ticket park(Vehicle vehicle) {
        String ticketId = UUID.randomUUID().toString();

        // Atomic dedup: claim the plate or fail. No lock needed.
        String existing = vehicleToTicketId.putIfAbsent(vehicle.numberPlate(), ticketId);
        if (existing != null) {
            throw new VehicleAlreadyParkedException(
                    "Vehicle " + vehicle.numberPlate() + " is already parked");
        }

        try {
            Slot slot = allocator.allocate(vehicle.type());
            Ticket ticket = Ticket.builder()
                    .id(ticketId)
                    .vehicle(vehicle)
                    .slot(slot)
                    .entryEpoch(clock.getAsLong())
                    .build();
            activeTickets.put(ticketId, ticket);
            return ticket;
        } catch (RuntimeException e) {
            // Roll back the dedup claim so the vehicle can retry.
            vehicleToTicketId.remove(vehicle.numberPlate(), ticketId);
            throw e;
        }
    }

    public Ticket unpark(String ticketId) {
        Ticket ticket = activeTickets.remove(ticketId);
        if (ticket == null) {
            throw new TicketNotFoundException("Ticket " + ticketId + " not found");
        }
        vehicleToTicketId.remove(ticket.getVehicle().numberPlate(), ticketId);

        ticket.setExitEpoch(clock.getAsLong());
        ticket.setAmountCharged(pricingStrategy.calculate(ticket));

        allocator.release(ticket.getSlot());
        return ticket;
    }
}
