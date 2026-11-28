import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class BookingFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTable flightTable;
    private int passengerCount;

    public BookingFrame(List<Flight> outbound, int passengerCount) {
        this.passengerCount = passengerCount;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 650, 350);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        String[] columns = {"Flight #", "From", "To", "Date", "Depart", "Arrive", "Price", "Seats Left"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (Flight f : outbound) {
            Object[] row = {
                f.getFlightNumber(),
                f.getFrom(),
                f.getTo(),
                f.getDate(),
                f.getDepartTime(),
                f.getArriveTime(),
                f.getPrice(),
                f.getSeatsLeft()
            };
            model.addRow(row);
        }

        flightTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(flightTable);
        scrollPane.setBounds(20, 40, 600, 180);
        contentPane.add(scrollPane);

        JButton bookBtn = new JButton("Confirm Booking");
        bookBtn.setBounds(230, 240, 180, 35);
        bookBtn.addActionListener(e -> {
            int selectedRow = flightTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(contentPane, "Please select a flight first.");
                return;
            }
            Flight chosenFlight = outbound.get(selectedRow);
            if (chosenFlight.getSeatsLeft() < passengerCount) {
                JOptionPane.showMessageDialog(contentPane, "Not enough seats available on this flight.");
                return;
            }

            // Open Passenger Details window
            PassengerDetailsFrame pdf = new PassengerDetailsFrame(chosenFlight, passengerCount);
            pdf.setVisible(true);
            dispose();
        });
        contentPane.add(bookBtn);
    }
}
