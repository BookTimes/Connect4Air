import java.time.LocalDate;

/**
 * One flight, e.g. DA101 DXB -> BOM on 25 Nov, 09:00 -> 13:30.
 */
public class Flight {
    private String flightNumber;
    private String from;
    private String to;
    private LocalDate date;
    private String departTime;
    private String arriveTime;
    private double price;      // price per passenger (AED)
    private int seatsLeft;

    public Flight(String flightNumber, String from, String to, LocalDate date,
                  String departTime, String arriveTime, double price, int seatsLeft) {
        this.flightNumber = flightNumber;
        this.from = from;
        this.to = to;
        this.date = date;
        this.departTime = departTime;
        this.arriveTime = arriveTime;
        this.price = price;
        this.seatsLeft = seatsLeft;
    }

    public String getFlightNumber() { return flightNumber; }
    public String getFrom()         { return from; }
    public String getTo()           { return to; }
    public LocalDate getDate()      { return date; }
    public double getPrice()        { return price; }
    public int getSeatsLeft()       { return seatsLeft; }
    public String getDepartTime() { return departTime;}
    public String getArriveTime() { return arriveTime;}

    /** Takes seats away. Returns false if there aren't enough. */
    public boolean reserveSeats(int count) {
        if (count > seatsLeft) {
            return false;
        }
        seatsLeft -= count;
        return true;
    }

    /** Gives seats back (used when a booking is cancelled). */
    public void releaseSeats(int count) {
        seatsLeft += count;
    }

    @Override
    public String toString() {
        return String.format("%-6s %s -> %s  %s  %s -> %s  AED %.0f  (%d seats left)",
                flightNumber, from, to, date, departTime, arriveTime, price, seatsLeft);
    }
}
