import java.awt.Font;
import java.awt.print.PrinterException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


public class PassengerDetailsFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;

    private JPanel[] passengerPanels = new JPanel[5];
    private JTextField[] nameFields = new JTextField[5];
    private JSpinner[] ageSpinners = new JSpinner[5];
    private JComboBox<String>[] genderBoxes = new JComboBox[5];

    private int visibleCount = 1;
    private int maxAllowed;
    private Flight flight;
    private JLabel totalPriceLabel;
    private JButton addPassengerBtn;

    @SuppressWarnings("unchecked")
    public PassengerDetailsFrame(Flight flight, int passengerCount) {
        this.flight = flight;
        this.maxAllowed = Math.min(Math.max(passengerCount, 1), 5);

        setTitle("Passenger Details - " + flight.getFlightNumber());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 560, 420);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel headerLabel = new JLabel("Enter Passenger Details (" + flight.getFrom() + " -> " + flight.getTo() + ")");
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        headerLabel.setBounds(25, 15, 450, 25);
        contentPane.add(headerLabel);

        // Create 5 passenger rows (only row 0 is visible at start)
        for (int i = 0; i < 5; i++) {
            JPanel rowPanel = new JPanel();
            rowPanel.setLayout(null);
            rowPanel.setBounds(25, 50 + (i * 42), 500, 35);

            JLabel pLabel = new JLabel("P" + (i + 1) + ":");
            pLabel.setBounds(0, 8, 30, 20);
            rowPanel.add(pLabel);

            JLabel nameLbl = new JLabel("Name:");
            nameLbl.setBounds(30, 8, 45, 20);
            rowPanel.add(nameLbl);

            nameFields[i] = new JTextField();
            nameFields[i].setBounds(75, 5, 160, 25);
            rowPanel.add(nameFields[i]);

            JLabel ageLbl = new JLabel("Age:");
            ageLbl.setBounds(245, 8, 35, 20);
            rowPanel.add(ageLbl);

            ageSpinners[i] = new JSpinner(new SpinnerNumberModel(25, 1, 120, 1));
            ageSpinners[i].setBounds(280, 5, 55, 25);
            rowPanel.add(ageSpinners[i]);

            JLabel genderLbl = new JLabel("Gender:");
            genderLbl.setBounds(345, 8, 50, 20);
            rowPanel.add(genderLbl);

            genderBoxes[i] = new JComboBox<>(new String[] {"Male", "Female", "Other"});
            genderBoxes[i].setBounds(398, 5, 95, 25);
            rowPanel.add(genderBoxes[i]);

            rowPanel.setVisible(i == 0); // Hide rows 2 to 5 initially
            passengerPanels[i] = rowPanel;
            contentPane.add(rowPanel);
        }

        // Reveal next passenger row one by one
        addPassengerBtn = new JButton("+ Add Next Passenger");
        addPassengerBtn.setBounds(25, 270, 180, 30);
        addPassengerBtn.setVisible(maxAllowed > 1);
        addPassengerBtn.addActionListener(e -> {
            if (visibleCount < maxAllowed) {
                passengerPanels[visibleCount].setVisible(true);
                visibleCount++;
                updateTotalPrice();
                if (visibleCount == maxAllowed) {
                    addPassengerBtn.setEnabled(false);
                }
            }
        });
        contentPane.add(addPassengerBtn);

        // Total Price Display
        totalPriceLabel = new JLabel();
        totalPriceLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        totalPriceLabel.setBounds(320, 270, 210, 30);
        updateTotalPrice();
        contentPane.add(totalPriceLabel);

        // Confirm & Print Ticket Button
        JButton finishBtn = new JButton("Complete Booking & Print Ticket");
        finishBtn.setBounds(150, 325, 250, 38);
        finishBtn.addActionListener(e -> processBooking());
        contentPane.add(finishBtn);
    }

    private void updateTotalPrice() {
        double total = flight.getPrice() * visibleCount;
        totalPriceLabel.setText(String.format("Total Price: $%.2f", total));
    }

    private void processBooking() {
        List<String> passengerSummaries = new ArrayList<>();
        List<String> namesOnly = new ArrayList<>();
        List<Integer> agesOnly = new ArrayList<>();
        List<String> gendersOnly = new ArrayList<>();

        for (int i = 0; i < visibleCount; i++) {
            String name = nameFields[i].getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(contentPane, "Please enter a name for Passenger " + (i + 1) + ".");
                return;
            }
            int age = (int) ageSpinners[i].getValue();
            String gender = (String) genderBoxes[i].getSelectedItem();

            namesOnly.add(name);
            agesOnly.add(age);
            gendersOnly.add(gender);
            passengerSummaries.add((i + 1) + ". " + name + " (Age: " + age + ", " + gender + ")");
        }

        double total = flight.getPrice() * visibleCount;
        int bookingId = databaseHandler.saveFullBooking(flight.getFlightNumber(), namesOnly, agesOnly, gendersOnly, total);

        showTicket(bookingId, passengerSummaries);
    }


    private void showTicket(int bookingId, List<String> passengers) {
        double total = flight.getPrice() * visibleCount;
        StringBuilder sb = new StringBuilder();
        sb.append("============================================\n");
        sb.append("           E-TICKET / BOARDING PASS         \n");
        sb.append("============================================\n");
        sb.append("Booking ID   : ").append(bookingId > 0 ? bookingId : "CONFIRMED").append("\n");
        sb.append("Flight       : ").append(flight.getFlightNumber()).append("\n");
        sb.append("Route        : ").append(flight.getFrom()).append(" ---> ").append(flight.getTo()).append("\n");
        sb.append("Date         : ").append(flight.getDate()).append("\n");
        sb.append("Departure    : ").append(flight.getDepartTime()).append(" | Arrival: ").append(flight.getArriveTime()).append("\n");
        sb.append("--------------------------------------------\n");
        sb.append("Passengers:\n");
        for (String p : passengers) {
            sb.append("  ").append(p).append("\n");
        }
        sb.append("--------------------------------------------\n");
        sb.append(String.format("Total Paid   : $%.2f\n", total));
        sb.append("============================================\n");

        String ticketText = sb.toString();

        JTextArea ticketArea = new JTextArea(ticketText);
        ticketArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        ticketArea.setEditable(false);

        Object[] options = {"Save Ticket", "Done"};
        int choice = JOptionPane.showOptionDialog(
            contentPane, new JScrollPane(ticketArea), "Ticket Confirmed",
            JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options
        );

        if (choice == JOptionPane.YES_OPTION) {
            String fileName = "Ticket_" + (bookingId > 0 ? bookingId : flight.getFlightNumber()) + ".txt";
            try {
                Files.writeString(Path.of(fileName), ticketText);
                JOptionPane.showMessageDialog(contentPane, "Ticket saved as " + fileName);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(contentPane, "Save Error: " + ex.getMessage());
            }
        }
        ViewBookingsFrame vb = new ViewBookingsFrame();
        vb.setVisible(true);
        dispose();
    }
}
