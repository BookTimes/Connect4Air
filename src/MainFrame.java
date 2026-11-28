import java.awt.EventQueue;
import java.awt.Font;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class MainFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JPanel loginPanel;
    private JTabbedPane tabbedPane;

    private JTextField username;
    private JPasswordField passwordField;

    private JTextField date_input;
    private JTextField return_date_input;
    private DefaultTableModel bookingsModel;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                MainFrame frame = new MainFrame();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public MainFrame() {
        setTitle("Flight Booking System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 740, 420);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        // 1. Login Panel (Shown initially)
        loginPanel = new JPanel();
        loginPanel.setBounds(0, 0, 725, 380);
        loginPanel.setLayout(null);
        contentPane.add(loginPanel);

        JLabel lblTitle = new JLabel("Log In");
        lblTitle.setFont(new Font("Lucida Grande", Font.PLAIN, 25));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setBounds(140, 20, 438, 55);
        loginPanel.add(lblTitle);

        JLabel lblUser = new JLabel("Username");
        lblUser.setBounds(285, 90, 81, 16);
        loginPanel.add(lblUser);

        username = new JTextField();
        username.setBounds(285, 108, 150, 26);
        loginPanel.add(username);

        JLabel lblPass = new JLabel("Password");
        lblPass.setBounds(285, 145, 81, 16);
        loginPanel.add(lblPass);

        passwordField = new JPasswordField();
        passwordField.setBounds(285, 163, 150, 26);
        loginPanel.add(passwordField);

        JButton loginBtn = new JButton("Login");
        loginBtn.setBounds(265, 205, 192, 29);
        loginPanel.add(loginBtn);

        // 2. Tabbed Pane (Hidden until login succeeds)
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setBounds(10, 10, 705, 360);
        tabbedPane.setVisible(false);
        contentPane.add(tabbedPane);

        // Tab 1: Search Flights
        tabbedPane.addTab("Search Flights", createSearchFlightsPanel());

        // Tab 2: View Bookings
        tabbedPane.addTab("View Bookings", createViewBookingsPanel());

        // Refresh bookings table whenever user clicks the "View Bookings" tab
        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == 1) {
                loadUserBookings();
            }
        });

        // Login Action
        loginBtn.addActionListener(e -> {
            User user = new User(username.getText().trim(), new String(passwordField.getPassword()));
            if (user.validateUser()) {
                JOptionPane.showMessageDialog(this, "Login Success");
                loginPanel.setVisible(false);
                tabbedPane.setVisible(true);
                loadUserBookings();
            } else {
                JOptionPane.showMessageDialog(this, "Login Failed: Wrong Credentials");
            }
        });
    }

    private JPanel createSearchFlightsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(null);

        JLabel header = new JLabel("Search Flights");
        header.setFont(new Font("SansSerif", Font.BOLD, 16));
        header.setHorizontalAlignment(SwingConstants.CENTER);
        header.setBounds(240, 10, 200, 25);
        panel.add(header);

        JLabel lblFrom = new JLabel("From");
        lblFrom.setBounds(210, 55, 50, 16);
        panel.add(lblFrom);

        JComboBox<String> from = new JComboBox<>(new String[] {"", "DXB", "BOM", "COK", "BLR"});
        from.setBounds(265, 50, 110, 27);
        panel.add(from);

        JLabel lblTo = new JLabel("To");
        lblTo.setBounds(210, 90, 50, 16);
        panel.add(lblTo);

        JComboBox<String> to = new JComboBox<>(new String[] {"", "DXB", "BOM", "COK", "BLR"});
        to.setBounds(265, 85, 110, 27);
        panel.add(to);

        JLabel lblPassengers = new JLabel("Passengers");
        lblPassengers.setBounds(185, 125, 75, 16);
        panel.add(lblPassengers);

        JSpinner p_count = new JSpinner(new SpinnerNumberModel(1, 1, 5, 1));
        p_count.setBounds(265, 120, 55, 26);
        panel.add(p_count);

        JLabel lblDate = new JLabel("Date");
        lblDate.setBounds(210, 160, 50, 16);
        panel.add(lblDate);

        date_input = new JTextField();
        date_input.setBounds(265, 155, 130, 26);
        panel.add(date_input);

        JLabel return_label = new JLabel("Return Date");
        return_label.setBounds(180, 220, 80, 16);
        return_label.setVisible(false);
        panel.add(return_label);

        return_date_input = new JTextField();
        return_date_input.setBounds(265, 215, 130, 26);
        return_date_input.setVisible(false);
        panel.add(return_date_input);

        JCheckBox book_return = new JCheckBox("Book Return");
        book_return.setBounds(262, 188, 128, 23);
        book_return.addActionListener(e -> {
            return_label.setVisible(book_return.isSelected());
            return_date_input.setVisible(book_return.isSelected());
        });
        panel.add(book_return);

        JButton searchBtn = new JButton("Search Flights");
        searchBtn.setBounds(245, 255, 190, 38);
        searchBtn.addActionListener(e -> {
            String fromCode = (String) from.getSelectedItem();
            String toCode = (String) to.getSelectedItem();
            if (fromCode == null || fromCode.isEmpty() || toCode == null || toCode.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select both From and To airports.");
                return;
            }
            try {
                LocalDate date = LocalDate.parse(date_input.getText().trim());
                int count = (int) p_count.getValue();
                List<Flight> outbound = databaseHandler.pickFlights(fromCode, toCode, date, count);

                if (outbound.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "No flights found for this route/date.");
                    return;
                }
                BookingFrame bf = new BookingFrame(outbound, count);
                bf.setVisible(true);
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid date in YYYY-MM-DD format.");
            }
        });
        panel.add(searchBtn);

        return panel;
    }

    private JPanel createViewBookingsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(null);

        String[] columns = {"Booking ID", "Flight #", "Route", "Date", "Passengers", "Total Price"};
        bookingsModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable bookingsTable = new JTable(bookingsModel);
        bookingsTable.getColumnModel().getColumn(4).setPreferredWidth(220);

        JScrollPane scrollPane = new JScrollPane(bookingsTable);
        scrollPane.setBounds(15, 15, 670, 255);
        panel.add(scrollPane);

        JButton refreshBtn = new JButton("Refresh Bookings");
        refreshBtn.setBounds(265, 280, 170, 32);
        refreshBtn.addActionListener(e -> loadUserBookings());
        panel.add(refreshBtn);

        return panel;
    }

    private void loadUserBookings() {
        bookingsModel.setRowCount(0);
        List<Object[]> bookings = databaseHandler.getBookingsByUser(User.userId);
        for (Object[] row : bookings) {
            bookingsModel.addRow(row);
        }
    }
}
