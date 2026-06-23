package org.satvik;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    static void main() {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        IO.println(String.format("Hello and welcome!"));

        for (int i = 1; i <= 5; i++) {
            //TIP Press <shortcut actionId="Debug"/> to start debugging your code. We have set one <icon src="AllIcons.Debugger.Db_set_breakpoint"/> breakpoint
            // for you, but you can always add more by pressing <shortcut actionId="ToggleLineBreakpoint"/>.
            IO.println("i = " + i);
        }
    }
}

/*
Design a Inter City Bus Service. (Similar to Uber Shuttle)
- Multiple pickup and drop point
- Handle concurrency
- Search algorithm for available seats
- Book Ticket and payment


Entity
Passenger - id, name
Bus - number, List<Seat>, currentLocation, routeId
Seat - id
SeatReservation - id, seat, reserved_at, reserved_by, status (EMPTY, BOOKED, RESERVED)
Booking - id, Passenger, Pickup, Drop, Bus, SeatReservation, date
Trip - id, List<Booking>
Route - id, List<Stops>
Payment - id, booking, amount, status

Models
Stop -> location, time

concurrency
1. One seat cannot be booked by multiple people
2. Mutliple buses can have the same route

service
bookingService -> reserveSeat(), confirmBooking(), cancelBooking();
paymentService -> pay(), refund()
TripService -> showTrips(fromDate, toDate), showRoutes(fromLocation, toLocation)

reserveSeat() will release the reservation if no confirmation call is made within 5 minutes
 */