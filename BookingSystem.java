import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Keeps all flights and bookings in memory and does the actual work:
 * searching, booking (one-way or return) and cancelling.
 */
public class BookingSystem {
    private List<Flight> flights = new ArrayList<>();
    private List<Booking> bookings = new ArrayList<>();
    private int nextBookingNumber = 1001;

    public BookingSystem() {
        loadSampleFlights();
    }

    /** Creates flights for the next 14 days so there is always something to book. */
    private void loadSampleFlights() {
        // {from, to, departTime, arriveTime, price}
        String[][] routes = {
            {"DXB", "BOM", "09:00", "13:30", "650"},
            {"BOM", "DXB", "15:00", "16:45", "620"},
            {"DXB", "COK", "08:15", "13:40", "700"},
            {"COK", "DXB", "19:30", "22:10", "680"},
            {"DXB", "DEL", "10:30", "15:20", "720"},
            {"DEL", "DXB", "17:00", "19:15", "690"},
            {"DXB", "LHR", "07:45", "12:05", "2400"},
            {"LHR", "DXB", "14:30", "00:40", "2350"},
            {"SHJ", "BLR", "11:00", "16:20", "600"},
            {"BLR", "SHJ", "18:00", "20:30", "580"},
        };

        LocalDate today = LocalDate.now();
        for (int day = 1; day <= 14; day++) {
            LocalDate date = today.plusDays(day);
            for (int r = 0; r < routes.length; r++) {
                String[] route = routes[r];
                String flightNo = "DA" + (101 + r);
                double price = Double.parseDouble(route[4]);
                flights.add(new Flight(flightNo, route[0], route[1], date,
                        route[2], route[3], price, 20));
            }
        }
    }

    /** All the airports we fly from, for showing in the menu. */
    public List<String> getAirports() {
        List<String> airports = new ArrayList<>();
        for (Flight f : flights) {
            if (!airports.contains(f.getFrom())) {
                airports.add(f.getFrom());
            }
        }
        return airports;
    }

    /** Flights on a route on one exact date that still have enough seats. */
    public List<Flight> searchFlights(String from, String to, LocalDate date, int passengers) {
        List<Flight> result = new ArrayList<>();
        for (Flight f : flights) {
            if (f.getFrom().equalsIgnoreCase(from)
                    && f.getTo().equalsIgnoreCase(to)
                    && f.getDate().equals(date)
                    && f.getSeatsLeft() >= passengers) {
                result.add(f);
            }
        }
        return result;
    }

    /**
     * Books a trip. Pass returnFlight = null for one-way.
     * Returns the booking, or null if something is wrong (no seats, bad dates).
     */
    public Booking book(String name, int passengers, Flight outbound, Flight returnFlight) {
        // The return flight must go back the opposite way, on or after the outbound date
        if (returnFlight != null) {
            boolean oppositeRoute = returnFlight.getFrom().equals(outbound.getTo())
                    && returnFlight.getTo().equals(outbound.getFrom());
            boolean dateOk = !returnFlight.getDate().isBefore(outbound.getDate());
            if (!oppositeRoute || !dateOk) {
                return null;
            }
        }

        // Reserve seats on both flights; undo the first one if the second fails
        if (!outbound.reserveSeats(passengers)) {
            return null;
        }
        if (returnFlight != null && !returnFlight.reserveSeats(passengers)) {
            outbound.releaseSeats(passengers);
            return null;
        }

        String id = "DOSA" + nextBookingNumber;
        nextBookingNumber++;
        Booking booking = new Booking(id, name, passengers, outbound, returnFlight);
        bookings.add(booking);
        return booking;
    }

    public Booking findBooking(String bookingId) {
        for (Booking b : bookings) {
            if (b.getBookingId().equalsIgnoreCase(bookingId)) {
                return b;
            }
        }
        return null;
    }

    /** Cancels a booking and gives the seats back. Returns false if not found. */
    public boolean cancelBooking(String bookingId) {
        Booking b = findBooking(bookingId);
        if (b == null) {
            return false;
        }
        b.getOutboundFlight().releaseSeats(b.getPassengers());
        if (b.isRoundTrip()) {
            b.getReturnFlight().releaseSeats(b.getPassengers());
        }
        bookings.remove(b);
        return true;
    }
}
