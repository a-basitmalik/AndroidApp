package Models.DAO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import Models.Campus;
import Models.Teacher;
import Models.Subject;
public class CampusDAO {
    private Connection connection;

    public CampusDAO(Connection connection) {
        this.connection = connection;
    }

    public List<Campus> getAllCampuses() {
        List<Campus> campuses = new ArrayList<>();
        String query = "SELECT * FROM Campus";

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int campusId = resultSet.getInt("CampusID");
                String campusName = resultSet.getString("CampusName");

                Campus campus = new Campus(campusId, campusName);
                campus.getTeachers().addAll(getTeachersByCampus(campusId));  
                campus.getSubjects().addAll(getSubjectsByCampus(campusId));  

                campuses.add(campus);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return campuses;
    }

    private List<Teacher> getTeachersByCampus(int campusId) {
        List<Teacher> teachers = new ArrayList<>();
        String query = "SELECT * FROM Teacher WHERE CampusID = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, campusId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    // FIXED: Use the correct Teacher constructor
                    String[] subjects = resultSet.getString("subjects").split(",");
                    String[] feedback = resultSet.getString("feedback") != null ? resultSet.getString("feedback").split(",") : new String[0];

                    teachers.add(new Teacher(
                        resultSet.getString("name"),
                        resultSet.getString("email"),
                        resultSet.getString("phone"),
                        resultSet.getFloat("rating"),
                        subjects,
                        feedback
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return teachers;
    }

    private List<Subject> getSubjectsByCampus(int campusId) {
        List<Subject> subjects = new ArrayList<>();
        String query = "SELECT * FROM Subjects WHERE CampusID = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, campusId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    // FIXED: Use the correct Subject constructor
                    subjects.add(new Subject(
                        resultSet.getString("subject_name"),
                        resultSet.getString("day"),
                        resultSet.getInt("teacher_id")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return subjects;
    }
}