import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;
 class Main {
    private static Scanner sc = new Scanner(System.in);
    private static BookingSystem system = new BookingSystem();

    public static void main(String[] args) {
        System.out.println("Welcome to DosaAir - Come for the flight, stay for the Dosa!");
        System.out.println("Enter username : ");
        String us = sc.nextLine();
        System.out.println("Enter password");
        String ps = sc.nextLine();
        User user = new User(us,ps);
        if (user.validateUser()){
            System.out.println("Logged in Successfully");
        }
        else {
            System.out.println("Wrong username / password");
            return;
        }
        while (true) {
            System.out.println();
            System.out.println("1. Book a flight");
            System.out.println("2. View my booking");
            System.out.println("3. Cancel a booking");
            System.out.println("4. Exit");
            int choice = readInt("Choose an option: ", 1, 4);

            if (choice == 1)      bookFlight();
            else if (choice == 2) viewBooking();
            else if (choice == 3) cancelBooking();
            else {
                System.out.println("Thank you for flying DosaAir!");
                break;
            }
        }
    }

    // ---------------- Menu actions ----------------

    private static void bookFlight() {
        System.out.println("\nAirports: " + system.getAirports());
        String from = readText("From (e.g. DXB): ").toUpperCase();
        String to = readText("To (e.g. BOM): ").toUpperCase();
        if (from.equals(to)) {
            System.out.println("From and To cannot be the same.");
            return;
        }

        System.out.println("1. One-way");
        System.out.println("2. Return");
        boolean roundTrip = readInt("Trip type: ", 1, 2) == 2;

        int passengers = readInt("Number of passengers (1-9): ", 1, 9);

        // ---- Outbound flight ----
        LocalDate departDate = readDate("Departure date (YYYY-MM-DD): ");
        Flight outbound = pickFlight(system.searchFlights(from, to, departDate, passengers));
        if (outbound == null) return;

        // ---- Return flight ----
        Flight returnFlight = null;
        if (roundTrip) {
            LocalDate returnDate = readDate("Return date (YYYY-MM-DD): ");
            if (returnDate.isBefore(departDate)) {
                System.out.println("Return date cannot be before the departure date.");
                return;
            }
            // Return goes the opposite way: to -> from
            returnFlight = pickFlight(system.searchFlights(to, from, returnDate, passengers));
            if (returnFlight == null) return;
        }

        String name = readText("Lead passenger name: ");

        // ---- Confirm ----
        double total = outbound.getPrice() * passengers;
        if (returnFlight != null) total += returnFlight.getPrice() * passengers;
        System.out.printf("Total price: AED %.2f. Confirm booking? (y/n): ", total);
        if (!sc.nextLine().trim().equalsIgnoreCase("y")) {
            System.out.println("Booking cancelled.");
            return;
        }

        Booking booking = system.book(name, passengers, outbound, returnFlight);
        if (booking == null) {
            System.out.println("Sorry, the booking could not be made (not enough seats).");
        } else {
            System.out.println("\nBooking confirmed! Save your Booking ID.");
            booking.printTicket();
        }
    }

    private static void viewBooking() {
        String id = readText("Enter Booking ID (e.g. DOSA1001): ");
        Booking b = system.findBooking(id);
        if (b == null) {
            System.out.println("No booking found with that ID.");
        } else {
            b.printTicket();
        }
    }

    private static void cancelBooking() {
        String id = readText("Enter Booking ID to cancel: ");
        if (system.cancelBooking(id)) {
            System.out.println("Booking " + id.toUpperCase() + " has been cancelled.");
        } else {
            System.out.println("No booking found with that ID.");
        }
    }

    // ---------------- Helpers ----------------

    /** Shows a list of flights and lets the user pick one. Returns null if none. */
    private static Flight pickFlight(List<Flight> options) {
        if (options.isEmpty()) {
            System.out.println("No flights available for that route/date.");
            return null;
        }
        System.out.println("\nAvailable flights:");
        for (int i = 0; i < options.size(); i++) {
            System.out.println((i + 1) + ". " + options.get(i));
        }
        int pick = readInt("Select flight: ", 1, options.size());
        return options.get(pick - 1);
    }

    private static String readText(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = sc.nextLine().trim();
            if (!s.isEmpty()) return s;
            System.out.println("Please type something.");
        }
    }

    private static int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            try {
                int n = Integer.parseInt(sc.nextLine().trim());
                if (n >= min && n <= max) return n;
            } catch (NumberFormatException e) {
                // fall through to the error message
            }
            System.out.println("Enter a number from " + min + " to " + max + ".");
        }
    }

    private static LocalDate readDate(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                LocalDate d = LocalDate.parse(sc.nextLine().trim());
                if (!d.isBefore(LocalDate.now())) return d;
                System.out.println("Date cannot be in the past.");
            } catch (DateTimeParseException e) {
                System.out.println("Use the format YYYY-MM-DD, e.g. " + LocalDate.now().plusDays(3));
            }
        }
    }
}
