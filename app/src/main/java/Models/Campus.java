package Models;
import java.util.ArrayList;
import java.util.List;

public class Campus {
    private int campusId;
    private String campusName;
    private List<Student> students;
    private List<Teacher> teachers;
    private List<Subject> subjects;

    public Campus(int campusId, String campusName) {
        this.campusId = campusId;
        this.campusName = campusName;
        this.students = new ArrayList<>();
        this.teachers = new ArrayList<>();
        this.subjects = new ArrayList<>();
    }

    public void addStudent(Student student) {
        students.add(student);
    }

    public void addTeacher(Teacher teacher) {
        teachers.add(teacher);
    }

    public void addSubject(Subject subject) {
        subjects.add(subject);
    }

    public int getCampusId() {
        return campusId;
    }

    public String getCampusName() {
        return campusName;
    }

    public List<Student> getStudents() {
        return students;
    }

    public List<Teacher> getTeachers() {
        return teachers;
    }

    public List<Subject> getSubjects() {
        return subjects;
    }
}