
package Models.DAO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import Models.Student;
public class StudentDAO {
    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        String query = "SELECT * FROM Students";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Student student = new Student.StudentBuilder(
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

                students.add(student);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }

    public static void main(String[] args) {
        StudentDAO studentDAO = new StudentDAO();
        List<Student> students = studentDAO.getAllStudents();

        for (Student student : students) {
            System.out.println("Student Name: " + student.getStudentName() + " | RFID: " + student.getRfid());
        }
    }
}
