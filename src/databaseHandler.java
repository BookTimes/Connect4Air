import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDate;
public  class databaseHandler{
    private static Connection con;
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(
                "jdbc:mysql://sql.freedb.tech:3306/freedb_1LvWciIL", 
                "u_KxPsf9", 
                "1FyDwU1tkDGu"
            );
        } catch (ClassNotFoundException e) {
            System.out.println("Driver not found - check your MySQL Connector JAR!");
        } catch (SQLException e) {
            System.out.println("Connection Error: " + e.getMessage());
        }
    }
public static int login(String us, String ps) {
    try (Statement st = con.createStatement()) {
    	ResultSet rs = st.executeQuery("SELECT user_id FROM user WHERE name='" + us + "' AND password='" + ps + "'");
    	if (rs.next()) {
    	    return rs.getInt("user_id");
    	}
    	return -1;
    } catch (Exception e) { return -1; }
}

public static List<Flight> pickFlights(String fromCode , String toCode , LocalDate date , int requiredSeats){
	List<Flight> matchingFlights = new ArrayList<>();
	String sql = "SELECT * FROM flights WHERE from_code = ? AND to_code = ? AND flight_date = ? AND seats_left >= ?";
	try (PreparedStatement pstmt = con.prepareStatement(sql)) {

	        pstmt.setString(1, fromCode);
	        pstmt.setString(2, toCode);
	        pstmt.setObject(3, date);
	        pstmt.setInt(4, requiredSeats);

	        try (ResultSet rs = pstmt.executeQuery()) {
	            while (rs.next()) {
	                Flight f = new Flight(
	                        rs.getString("flight_number"),
	                        rs.getString("from_code"),
	                        rs.getString("to_code"),
	                        rs.getDate("flight_date").toLocalDate(),
	                        rs.getString("depart_time"),
	                        rs.getString("arrive_time"),
	                        rs.getDouble("price"),
	                        rs.getInt("seats_left")
	                    );
	                matchingFlights.add(f);
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return matchingFlights;
}

public static int saveFullBooking(String flightNumber, List<String> names,
        List<Integer> ages, List<String> genders, double totalPrice) {
String updateSql = "UPDATE flights SET seats_left = seats_left - ? WHERE flight_number = ? AND seats_left >= ?";
String insertSql = "INSERT INTO bookings (flight_number, user_id, " +
"p1_name, p1_age, p1_gender, p2_name, p2_age, p2_gender, " +
"p3_name, p3_age, p3_gender, p4_name, p4_age, p4_gender, " +
"p5_name, p5_age, p5_gender, total_price) " +
"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

System.out.println("DEBUG -> Flight: " + flightNumber + " | UserID: " + User.userId + " | Con: " + con);

try {
PreparedStatement updateStmt = con.prepareStatement(updateSql);
updateStmt.setInt(1, names.size());
updateStmt.setString(2, flightNumber);
updateStmt.setInt(3, names.size());

int updatedRows = updateStmt.executeUpdate();
System.out.println("DEBUG -> Updated flight rows: " + updatedRows);
if (updatedRows == 0) return -1;

PreparedStatement insertStmt = con.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
insertStmt.setString(1, flightNumber);
insertStmt.setInt(2, User.userId);

for (int i = 0; i < 5; i++) {
    int col = 3 + (i * 3);
    insertStmt.setObject(col,     (i < names.size())   ? names.get(i).trim() : null);
    insertStmt.setObject(col + 1, (i < ages.size())    ? ages.get(i)         : null);
    insertStmt.setObject(col + 2, (i < genders.size()) ? genders.get(i)      : null);
}


insertStmt.setDouble(18, totalPrice);
insertStmt.executeUpdate();
System.out.println("DEBUG -> Booking inserted!");

ResultSet keys = insertStmt.getGeneratedKeys();
if (keys.next()) return keys.getInt(1);

} catch (Exception e) {
e.printStackTrace();
}
return -1;
}


public static List<Object[]> getBookingsByUser(int userId) {
    List<Object[]> list = new ArrayList<>();
    String sql = "SELECT b.booking_id, b.flight_number, f.from_code, f.to_code, f.flight_date, " +
                 "b.p1_name, b.p2_name, b.p3_name, b.p4_name, b.p5_name, b.total_price " +
                 "FROM bookings b " +
                 "JOIN flights f ON b.flight_number = f.flight_number " +
                 "WHERE b.user_id = ?";

    try (PreparedStatement pstmt = con.prepareStatement(sql)) {

        pstmt.setInt(1, userId);

        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                List<String> names = new ArrayList<>();
                for (int i = 1; i <= 5; i++) {
                    String pName = rs.getString("p" + i + "_name");
                    if (pName != null && !pName.trim().isEmpty()) {
                        names.add(pName);
                    }
                }

                Object[] row = {
                    rs.getInt("booking_id"),
                    rs.getString("flight_number"),
                    rs.getString("from_code") + " -> " + rs.getString("to_code"),
                    rs.getDate("flight_date"),
                    String.join(", ", names),
                    String.format("$%.2f", rs.getDouble("total_price"))
                };
                list.add(row);
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return list;
}




//     public static void main(String args[]){
//         boolean i = login("Hiesh", "12345");
//         System.out.println(i);
// }

}