import java.sql.*;

public class DBCheck {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/madgarage?serverTimezone=UTC";
        String user = "root";
        String password = "root";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            System.out.println("Connected to database.");
            
            // Check orders and items
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT o.id, o.status, o.user_id, i.product_id, p.part_name, p.is_returnable " +
                                                "FROM orders o " +
                                                "JOIN order_items i ON o.id = i.order_id " +
                                                "JOIN products p ON i.product_id = p.id " +
                                                "WHERE o.status = 'DELIVERED' OR o.id IN (43, 44)")) {
                while (rs.next()) {
                    System.out.println("Order ID=" + rs.getLong("id") + 
                                       ", UserID=" + rs.getLong("user_id") +
                                       ", Status=" + rs.getString("status") + 
                                       ", Product=" + rs.getString("part_name") + 
                                       ", Returnable=" + rs.getBoolean("is_returnable"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
