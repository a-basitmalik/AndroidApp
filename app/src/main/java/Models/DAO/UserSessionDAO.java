package Models.DAO;
import java.sql.*;
import Models.Teacher;
import Models.Student;

public class UserSessionDAO {

    // Constants for different user roles
    public static final String ROLE_STUDENT = "student";
    public static final String ROLE_TEACHER = "teacher";
    public static final String ROLE_ADMIN = "admin";

    /**
     * Authenticate a user against the database
     * @param email User's email
     * @param password User's password
     * @return String array containing [role, email, userNo] if authenticated, null otherwise
     */
    public static String[] authenticateUser(String email, String password) {
        String[] userData = null;

        // First try to authenticate as a student
        userData = authenticateStudent(email, password);
        if (userData != null) {
            return userData;
        }

        // Then try to authenticate as a teacher
        userData = authenticateTeacher(email, password);
        if (userData != null) {
            return userData;
        }

        // Additional authentication methods can be added here (e.g., admin)

        return null; // Authentication failed
    }

    /**
     * Attempt to authenticate as a student
     */
    private static String[] authenticateStudent(String email, String password) {
        String query = "SELECT StudentID, student_name, phone_number FROM Students WHERE phone_number = ? AND Password = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email); // Using phone_number as email for students
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String[] userData = new String[3];
                    userData[0] = ROLE_STUDENT; // Role
                    userData[1] = rs.getString("phone_number"); // Email/Phone
                    userData[2] = String.valueOf(rs.getInt("StudentID")); // User Number
                    return userData;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Attempt to authenticate as a teacher
     */
    private static String[] authenticateTeacher(String email, String password) {
        String query = "SELECT id, name, email FROM Teacher WHERE email = ? AND password = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String[] userData = new String[3];
                    userData[0] = ROLE_TEACHER; // Role
                    userData[1] = rs.getString("email"); // Email
                    userData[2] = String.valueOf(rs.getInt("id")); // User Number
                    return userData;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Get student profile data
     * @param studentId The student ID
     * @return Student object if found, null otherwise
     */
    public static Student getStudentProfile(int studentId) {
        String query = "SELECT * FROM Students WHERE StudentID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, studentId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Student.StudentBuilder(
                            rs.getString("student_name"),
                            rs.getString("phone_number"),
                            rs.getString("Password")
                    )
                            .rfid(rs.getInt("RFID"))
                            .pictureUrl(rs.getString("picture_url"))
                            .daysAttended(rs.getInt("DaysAttended"))
                            .totalDays(rs.getInt("TotalDays"))
                            .fine(rs.getInt("Fine"))
                            .studentId(String.valueOf(rs.getInt("StudentID")))

                            .absenteeId(rs.getString("AbsenteeID"))
                            .year(rs.getInt("year"))
                            .campusId(rs.getInt("campusid"))
                            .feeAmount(rs.getInt("FeeAmount"))
                            .build();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Get teacher profile data
     * @param teacherId The teacher ID
     * @return Teacher object if found, null otherwise
     */
    public static Teacher getTeacherProfile(int teacherId) {
        String query = "SELECT * FROM Teacher WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, teacherId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("name");
                    String email = rs.getString("email");
                    String phone = rs.getString("phone");
                    float rating = rs.getFloat("rating");
                    String[] subjects = rs.getString("subjects").split(",");
                    String[] feedback = rs.getString("feedback") != null ? rs.getString("feedback").split(",") : new String[0];

                    return new Teacher(name, email, phone, rating, subjects, feedback, teacherId);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}