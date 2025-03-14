package Models.DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import Models.DAO.DatabaseConnection;
import Models.Teacher;
public class TeacherDAO {
    public static List<Teacher> getAllTeachers() {
        List<Teacher> teachers = new ArrayList<>();
        String query = "SELECT * FROM Teacher";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                String phone = rs.getString("phone");
                float rating = rs.getFloat("rating");
                String[] subjects = rs.getString("subjects").split(",");
                String[] feedback = rs.getString("feedback") != null ? rs.getString("feedback").split(",") : new String[0];

                teachers.add(new Teacher(name, email, phone, rating, subjects, feedback, id));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return teachers;
    }
}
