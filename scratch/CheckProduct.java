import java.sql.*;

public class CheckProduct {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/madgarage?serverTimezone=UTC";
        String user = "root";
        String password = "root";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            String query = "SELECT p.id, p.part_name, p.is_returnable FROM products p JOIN order_items oi ON p.id = oi.product_id WHERE oi.order_id = 44;";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    System.out.println("Product ID: " + rs.getLong("id"));
                    System.out.println("Part Name: " + rs.getString("part_name"));
                    System.out.println("Is Returnable (DB): " + rs.getBoolean("is_returnable"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
