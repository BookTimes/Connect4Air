/**
 * A booking. If returnFlight is null, it is a one-way trip.
 */
public class Booking {
    private String bookingId;
    private String passengerName;
    private int passengers;
    private Flight outboundFlight;
    private Flight returnFlight;   // null for one-way

    public Booking(String bookingId, String passengerName, int passengers,
                   Flight outboundFlight, Flight returnFlight) {
        this.bookingId = bookingId;
        this.passengerName = passengerName;
        this.passengers = passengers;
        this.outboundFlight = outboundFlight;
        this.returnFlight = returnFlight;
    }

    public String getBookingId()      { return bookingId; }
    public int getPassengers()        { return passengers; }
    public Flight getOutboundFlight() { return outboundFlight; }
    public Flight getReturnFlight()   { return returnFlight; }

    public boolean isRoundTrip() {
        return returnFlight != null;
    }

    public double getTotalPrice() {
        double total = outboundFlight.getPrice() * passengers;
        if (isRoundTrip()) {
            total += returnFlight.getPrice() * passengers;
        }
        return total;
    }

    public void printTicket() {
        System.out.println("=========================================");
        System.out.println("            DOSAAIR E-TICKET");
        System.out.println("=========================================");
        System.out.println("Booking ID : " + bookingId);
        System.out.println("Name       : " + passengerName);
        System.out.println("Passengers : " + passengers);
        System.out.println("Trip type  : " + (isRoundTrip() ? "Return" : "One-way"));
        System.out.println("-----------------------------------------");
        System.out.println("Outbound   : " + outboundFlight);
        if (isRoundTrip()) {
            System.out.println("Return     : " + returnFlight);
        }
        System.out.println("-----------------------------------------");
        System.out.printf("TOTAL      : AED %.2f%n", getTotalPrice());
        System.out.println("=========================================");
    }
}
