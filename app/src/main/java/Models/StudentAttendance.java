package Models;


public class StudentAttendance {
    private int rfid;
    private String studentName;
    private String studentId;
    private boolean isPresent;
    private String date;

    public StudentAttendance(int rfid, String studentName, String studentId, boolean isPresent, String date) {
        this.rfid = rfid;
        this.studentName = studentName;
        this.studentId = studentId;
        this.isPresent = isPresent;
        this.date = date;
    }


    public int getRfid() { return rfid; }
    public void setRfid(int rfid) { this.rfid = rfid; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public boolean isPresent() { return isPresent; }
    public void setPresent(boolean present) { isPresent = present; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
}