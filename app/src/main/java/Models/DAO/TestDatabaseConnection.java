import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import Models.DAO.DatabaseConnection;

public class TestDatabaseConnection {
    public static void main(String[] args) {
        Connection conn = DatabaseConnection.getConnection();
        
        if (conn != null) {
            System.out.println("✅ Database connection successful!");
            
            try {
                // Create a statement
                Statement stmt = conn.createStatement();
                String query = "SELECT student_name, RFID FROM Students LIMIT 5";  // Fetch first 5 students
                
                // Execute the query
                ResultSet rs = stmt.executeQuery(query);

                // Print header
                System.out.println("\n📌 Fetching student data from database:");
                System.out.println("=======================================");

                // Process and print results
                while (rs.next()) {
                    String studentName = rs.getString("student_name");
                    int rfid = rs.getInt("RFID");
                    System.out.println("👤 Student: " + studentName + " | 🆔 RFID: " + rfid);
                }

                // Close resources
                rs.close();
                stmt.close();
                conn.close();
                
                System.out.println("\n✅ Data retrieval successful!");
            } catch (SQLException e) {
                System.out.println("❌ Error fetching data!");
                e.printStackTrace();
            }
        } else {
            System.out.println("❌ Failed to connect to the database.");
        }
    }
}