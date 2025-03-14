package Models;


import java.time.LocalTime;
import java.util.Date;

public class SubjectAttendance {
    private int attendanceId;
    private int rfid;
    private int subjectId;
    private AttendanceStatus attendanceStatus;
    private Date date;
    private LocalTime time;

    public enum AttendanceStatus {
        PRESENT,
        ABSENT,
        LEAVE
    }

    private SubjectAttendance(SubjectAttendanceBuilder builder) {
        this.attendanceId = builder.attendanceId;
        this.rfid = builder.rfid;
        this.subjectId = builder.subjectId;
        this.attendanceStatus = builder.attendanceStatus;
        this.date = builder.date;
        this.time = builder.time;
    }

    public static class SubjectAttendanceBuilder {
        private int attendanceId;
        private int rfid;
        private int subjectId;
        private AttendanceStatus attendanceStatus;
        private Date date;
        private LocalTime time;

        public SubjectAttendanceBuilder(int rfid, int subjectId) {
            this.rfid = rfid;
            this.subjectId = subjectId;
        }

        public SubjectAttendanceBuilder attendanceId(int attendanceId) {
            this.attendanceId = attendanceId;
            return this;
        }

        public SubjectAttendanceBuilder attendanceStatus(AttendanceStatus status) {
            this.attendanceStatus = status;
            return this;
        }

        public SubjectAttendanceBuilder date(Date date) {
            this.date = date;
            return this;
        }

        public SubjectAttendanceBuilder time(LocalTime time) {
            this.time = time;
            return this;
        }

        public SubjectAttendance build() {
            return new SubjectAttendance(this);
        }
    }


    public int getAttendanceId() { return attendanceId; }
    public int getRfid() { return rfid; }
    public int getSubjectId() { return subjectId; }
    public AttendanceStatus getAttendanceStatus() { return attendanceStatus; }
    public Date getDate() { return date; }
    public LocalTime getTime() { return time; }
}
