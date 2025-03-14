package Models;

import java.util.ArrayList;
import java.util.List;

public class Student {
    private int rfid;
    private String studentName;
    private String phoneNumber;
    private String pictureUrl;
    private int daysAttended;
    private String password;
    private int totalDays;
    private int fine;
    private String studentId;
    private String absenteeId;
    private int year;
    private int campusId;
    private int feeAmount;
    private List<String> subjects;

    private Student(StudentBuilder builder) {
        this.rfid = builder.rfid;
        this.studentName = builder.studentName;
        this.phoneNumber = builder.phoneNumber;
        this.pictureUrl = builder.pictureUrl;
        this.daysAttended = builder.daysAttended;
        this.password = builder.password;
        this.totalDays = builder.totalDays;
        this.fine = builder.fine;
        this.studentId = String.valueOf(builder.studentId);

        this.absenteeId = builder.absenteeId;
        this.year = builder.year;
        this.campusId = builder.campusId;
        this.feeAmount = builder.feeAmount;
        this.subjects = builder.subjects;
    }

    public Student(String studentId, String studentName, String phoneNumber, int year) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.phoneNumber = phoneNumber;
        this.year = year;
    }
    public Student(int rfid, String studentName, String phoneNumber, int year) {
        this.rfid = rfid;
        this.studentName = studentName;
        this.phoneNumber = phoneNumber;
        this.year = year;
    }
    public static class StudentBuilder {

        private int rfid;
        private String studentName;
        private String phoneNumber;
        private String pictureUrl = "";
        private int daysAttended = 0;
        private String password;
        private int totalDays = 0;
        private int fine = 0;
        private String studentId;
        private String absenteeId = "";
        private int year;
        private int campusId;
        private int feeAmount;
        private List<String> subjects = new ArrayList<>();

        public StudentBuilder(String studentName, String phoneNumber, String password) {
            this.studentName = studentName;
            this.phoneNumber = phoneNumber;
            this.password = password;
        }



        public StudentBuilder rfid(int rfid) {
            this.rfid = rfid;
            return this;
        }

        public StudentBuilder pictureUrl(String pictureUrl) {
            this.pictureUrl = pictureUrl;
            return this;
        }

        public StudentBuilder daysAttended(int daysAttended) {
            this.daysAttended = daysAttended;
            return this;
        }

        public StudentBuilder totalDays(int totalDays) {
            this.totalDays = totalDays;
            return this;
        }

        public StudentBuilder fine(int fine) {
            this.fine = fine;
            return this;
        }

        public StudentBuilder studentId(String studentId) {
            this.studentId = studentId;
            return this;
        }

        public StudentBuilder absenteeId(String absenteeId) {
            this.absenteeId = absenteeId;
            return this;
        }

        public StudentBuilder year(int year) {
            this.year = year;
            return this;
        }

        public StudentBuilder campusId(int campusId) {
            this.campusId = campusId;
            return this;
        }

        public StudentBuilder feeAmount(int feeAmount) {
            this.feeAmount = feeAmount;
            return this;
        }

        public StudentBuilder subjects(List<String> subjects) {
            this.subjects = new ArrayList<>(subjects);
            return this;
        }

        public Student build() {
            return new Student(this);
        }
    }


    public int getRfid() { return rfid; }
    public String getStudentName() { return studentName; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getPictureUrl() { return pictureUrl; }
    public int getDaysAttended() { return daysAttended; }
    public String getPassword() { return password; }
    public int getTotalDays() { return totalDays; }
    public int getFine() { return fine; }
    public String getStudentId() { return studentId; }
    public String getAbsenteeId() { return absenteeId; }
    public int getYear() { return year; }
    public int getCampusId() { return campusId; }
    public int getFeeAmount() { return feeAmount; }
    public List<String> getSubjects() { return new ArrayList<>(subjects); }


    public double getAttendancePercentage() {
        if (totalDays == 0) return 0.0;
        return ((double) daysAttended / totalDays) * 100;
    }

    public boolean hasPendingFees() {
        return feeAmount > 0;
    }

    public boolean hasFines() {
        return fine > 0;
    }
}