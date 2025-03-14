package Models;

import java.util.Calendar;

public class Subject {
    private int subjectId;
    private String subjectName;
    private String day;
    private Calendar time;
    private int teacherId;
    private int campusId;
    private int year;
    private String teacherName;
    private int studentCount;


    public Subject(String subjectName, String day, Calendar time, int teacherId, int campusId, int year, String teacherName) {
        this.subjectName = subjectName;
        this.day = day;
        this.time = time;
        this.teacherId = teacherId;
        this.campusId = campusId;
        this.year = year;
        this.teacherName = teacherName;
    }


    public Subject(String subjectName, String teacherName, int studentCount) {
        this.subjectName = subjectName;
        this.teacherName = teacherName;
        this.studentCount = studentCount;


        this.subjectId = 0;
        this.day = null;
        this.time = null;
        this.teacherId = 0;
        this.campusId = 0;
        this.year = 0;
    }


    private Subject(SubjectBuilder builder) {
        this.subjectId = builder.subjectId;
        this.subjectName = builder.subjectName;
        this.day = builder.day;
        this.time = builder.time;
        this.teacherId = builder.teacherId;
        this.campusId = builder.campusId;
        this.year = builder.year;
        this.teacherName = builder.teacherName;
        this.studentCount = builder.studentCount;
    }


    public static class SubjectBuilder {
        private int subjectId;
        private String subjectName;
        private String day;
        private Calendar time;
        private int teacherId;
        private int campusId;
        private int year;
        private String teacherName;
        private int studentCount;

        public SubjectBuilder(String subjectName) {
            this.subjectName = subjectName;
        }

        public SubjectBuilder subjectId(int subjectId) {
            this.subjectId = subjectId;
            return this;
        }

        public SubjectBuilder teacherName(String teacherName) {
            this.teacherName = teacherName;
            return this;
        }

        public SubjectBuilder day(String day) {
            this.day = day;
            return this;
        }

        public SubjectBuilder time(int hourOfDay, int minute) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            this.time = calendar;
            return this;
        }

        public SubjectBuilder teacherId(int teacherId) {
            this.teacherId = teacherId;
            return this;
        }

        public SubjectBuilder campusId(int campusId) {
            this.campusId = campusId;
            return this;
        }

        public SubjectBuilder year(int year) {
            this.year = year;
            return this;
        }

        public SubjectBuilder studentCount(int studentCount) {
            this.studentCount = studentCount;
            return this;
        }

        public Subject build() {
            return new Subject(this);
        }
    }


    public int getSubjectId() {
        return subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public String getDay() {
        return day;
    }

    public Calendar getTime() {
        return time;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public int getCampusId() {
        return campusId;
    }

    public int getYear() {
        return year;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public int getStudentCount() {
        return studentCount;
    }

    public String getName() {
        return this.subjectName; // Ensure this variable exists in the class
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public void setTime(Calendar time) {
        this.time = time;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    public void setCampusId(int campusId) {
        this.campusId = campusId;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public void setStudentCount(int studentCount) {
        this.studentCount = studentCount;
    }
}