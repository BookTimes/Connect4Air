import java.awt.Font;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class ViewBookingsFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTable bookingsTable;

    public ViewBookingsFrame() {
    	int userId = User.userId;
        setTitle("My Bookings");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 720, 380);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel titleLabel = new JLabel("Bookings for User ID: " + userId);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        titleLabel.setBounds(20, 15, 300, 25);
        contentPane.add(titleLabel);

        String[] columns = {"Booking ID", "Flight #", "Route", "Date", "Passengers", "Total Price"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };


        List<Object[]> bookings = databaseHandler.getBookingsByUser(User.userId);
        for (Object[] row : bookings) {
            model.addRow(row);
        }

        bookingsTable = new JTable(model);
        bookingsTable.getColumnModel().getColumn(4).setPreferredWidth(220);

        JScrollPane scrollPane = new JScrollPane(bookingsTable);
        scrollPane.setBounds(20, 50, 665, 220);
        contentPane.add(scrollPane);

        JButton closeBtn = new JButton("Close");
        closeBtn.setBounds(290, 290, 120, 35);
        closeBtn.addActionListener(e -> dispose());
        contentPane.add(closeBtn);
    }
}
