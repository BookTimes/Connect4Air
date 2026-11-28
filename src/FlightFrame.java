import java.awt.EventQueue;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.SwingConstants;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class FlightFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField date_input;
	private JTextField return_date_input;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FlightFrame frame = new FlightFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public FlightFrame() {
	    BookingSystem system = new BookingSystem();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Search Flights");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(173, 6, 117, 16);
		contentPane.add(lblNewLabel);
		
		JLabel lblNewLabel_2 = new JLabel("From");
		lblNewLabel_2.setBounds(116, 49, 43, 16);
		contentPane.add(lblNewLabel_2);
		
		JLabel lblNewLabel_2_1 = new JLabel("To");
		lblNewLabel_2_1.setBounds(116, 78, 43, 16);
		contentPane.add(lblNewLabel_2_1);
		
		JLabel lblNewLabel_2_1_1 = new JLabel("Date");
		lblNewLabel_2_1_1.setBounds(116, 132, 43, 16);
		contentPane.add(lblNewLabel_2_1_1);
		
		date_input = new JTextField();
		date_input.setColumns(10);
		date_input.setBounds(160, 127, 130, 26);
		contentPane.add(date_input);
		
		JLabel return_label = new JLabel("Return Date");
		return_label.setBounds(79, 182, 80, 16);
		JCheckBox book_return = new JCheckBox("Book Return");
		book_return.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				return_label.setVisible(book_return.isSelected());
				return_date_input.setVisible(book_return.isSelected());
			}
		});
		book_return.setBounds(157, 155, 128, 23);
		contentPane.add(book_return);
		
		contentPane.add(return_label);
		return_label.setVisible(false);
		
		return_date_input = new JTextField();
		return_date_input.setColumns(10);
		return_date_input.setBounds(160, 177, 130, 26);
		contentPane.add(return_date_input);
		
		JComboBox<String> from = new JComboBox<>();
		from.setModel(new DefaultComboBoxModel<>(new String[] {"", "DXB", "BOM", "COK", "BLR"}));
		from.setBounds(160, 45, 86, 27);
		contentPane.add(from);
		
		JComboBox<String> to = new JComboBox<>();
		to.setModel(new DefaultComboBoxModel<>(new String[] {"", "DXB", "BOM", "COK", "BLR"}));
		to.setBounds(160, 74, 86, 27);
		contentPane.add(to);

		return_date_input.setVisible(false);	
		
		// Set default min=1, initial=1 so users can't book 0 or negative passengers
		JSpinner p_count = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
		p_count.setBounds(173, 100, 45, 26);
		contentPane.add(p_count);
		
		JLabel lblNewLabel_2_1_1_1 = new JLabel("Passenger Count");
		lblNewLabel_2_1_1_1.setBounds(54, 106, 105, 16);
		contentPane.add(lblNewLabel_2_1_1_1);
		
		JButton btnNewButton = new JButton("Search Flights");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					LocalDate date = LocalDate.parse(date_input.getText().trim());
					List<Flight> outbound = databaseHandler.pickFlights(
						    (String) from.getSelectedItem(),
						    (String) to.getSelectedItem(),
						    date,
						    (int) p_count.getValue()
						);
					int count = Math.min((int) p_count.getValue(), 5);
					BookingFrame bf = new BookingFrame(outbound, count);
					bf.setVisible(true);
					dispose();

				} catch (DateTimeParseException ex) {
					JOptionPane.showMessageDialog(contentPane, "Please enter a valid date in YYYY-MM-DD format.");
				}
			}
		});
		btnNewButton.setBounds(129, 215, 188, 40);
		contentPane.add(btnNewButton);	
	}
}
