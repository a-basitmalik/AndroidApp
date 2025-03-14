package Models.DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import Models.Subject;
import Models.DAO.DatabaseConnection;

public class SubjectDAO {
    public static List<Subject> getAllSubjects() {
        List<Subject> subjects = new ArrayList<>();
        String query = "SELECT * FROM Subjects";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int subjectId = rs.getInt("subject_id");
                String subjectName = rs.getString("subject_name");
                String day = rs.getString("day");
                Time sqlTime = rs.getTime("time");
                Calendar time = Calendar.getInstance();
                time.setTime(sqlTime);

                int teacherId = rs.getInt("teacher_id");
                int campusId = rs.getInt("campus_id");
                int year = rs.getInt("year");
                String teacherName = rs.getString("teacher_name");
                int studentCount = rs.getInt("student_count");

                Subject subject = new Subject.SubjectBuilder(subjectName)
                        .subjectId(subjectId)
                        .day(day)
                        .time(time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE))
                        .teacherId(teacherId)
                        .campusId(campusId)
                        .year(year)
                        .teacherName(teacherName)
                        .studentCount(studentCount)
                        .build();

                subjects.add(subject);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return subjects;
    }
}
