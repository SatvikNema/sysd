package com.satvik.parkinglot;

import com.satvik.parkinglot.allocator.FirstAvailableAllocator;
import com.satvik.parkinglot.allocator.SlotAllocator;
import com.satvik.parkinglot.exception.NoSlotAvailableException;
import com.satvik.parkinglot.exception.TicketNotFoundException;
import com.satvik.parkinglot.exception.VehicleAlreadyParkedException;
import com.satvik.parkinglot.model.Slot;
import com.satvik.parkinglot.model.Ticket;
import com.satvik.parkinglot.model.Vehicle;
import com.satvik.parkinglot.model.VehicleType;
import com.satvik.parkinglot.pricing.FlatHourlyPricingStrategy;
import com.satvik.parkinglot.pricing.PricingStrategy;
import com.satvik.parkinglot.service.ParkingLot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.LongSupplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ParkingLotTest {

    private ParkingLot lot;
    private long[] now;

    @BeforeEach
    void setUp() {
        List<Slot> slots = List.of(
                new Slot("FW-0", VehicleType.FOUR_WHEELER, 0),
                new Slot("TW-0", VehicleType.TWO_WHEELER, 0)
        );
        SlotAllocator allocator = new FirstAvailableAllocator(slots);

        Map<VehicleType, Double> rates = new EnumMap<>(VehicleType.class);
        rates.put(VehicleType.TWO_WHEELER, 10.0);
        rates.put(VehicleType.FOUR_WHEELER, 20.0);
        PricingStrategy pricing = new FlatHourlyPricingStrategy(rates);

        now = new long[]{1_000L};
        LongSupplier clock = () -> now[0];

        lot = new ParkingLot(allocator, pricing, clock);
    }

    @Test
    void park_and_unpark_happy_path() {
        Vehicle car = new Vehicle("KA-01-1234", VehicleType.FOUR_WHEELER);

        Ticket ticket = lot.park(car);
        assertNotNull(ticket.getId());
        assertEquals("FW-0", ticket.getSlot().id());
        assertEquals(1_000L, ticket.getEntryEpoch());

        now[0] += 3_600; // exactly 1 hour
        Ticket done = lot.unpark(ticket.getId());

        assertEquals(20.0, done.getAmountCharged());
        assertEquals(4_600L, done.getExitEpoch());
    }

    @Test
    void partial_hour_rounds_up() {
        Vehicle car = new Vehicle("KA-01-1234", VehicleType.FOUR_WHEELER);
        Ticket ticket = lot.park(car);

        now[0] += 3_600 + 1; // 1 hour + 1 second → charged as 2 hours
        Ticket done = lot.unpark(ticket.getId());

        assertEquals(40.0, done.getAmountCharged());
    }

    @Test
    void double_park_is_rejected() {
        Vehicle car = new Vehicle("KA-01-1234", VehicleType.FOUR_WHEELER);
        lot.park(car);
        assertThrows(VehicleAlreadyParkedException.class, () -> lot.park(car));
    }

    @Test
    void no_slot_available_throws() {
        Vehicle car1 = new Vehicle("KA-01-1111", VehicleType.FOUR_WHEELER);
        Vehicle car2 = new Vehicle("KA-01-2222", VehicleType.FOUR_WHEELER);
        lot.park(car1);
        assertThrows(NoSlotAvailableException.class, () -> lot.park(car2));
    }

    @Test
    void released_slot_is_reusable() {
        Vehicle car1 = new Vehicle("KA-01-1111", VehicleType.FOUR_WHEELER);
        Vehicle car2 = new Vehicle("KA-01-2222", VehicleType.FOUR_WHEELER);

        Ticket t1 = lot.park(car1);
        now[0] += 3_600;
        lot.unpark(t1.getId());

        Ticket t2 = lot.park(car2);
        assertEquals("FW-0", t2.getSlot().id());
    }

    @Test
    void unknown_ticket_throws() {
        assertThrows(TicketNotFoundException.class, () -> lot.unpark("does-not-exist"));
    }
}
