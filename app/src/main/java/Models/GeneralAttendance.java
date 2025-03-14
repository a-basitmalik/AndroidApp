package Models;

import java.time.LocalTime;
import java.util.Date;

public class GeneralAttendance {
    private int rfid;
    private Date date;
    private LocalTime time;
    private String status;

    private GeneralAttendance(GeneralAttendanceBuilder builder) {
        this.rfid = builder.rfid;
        this.date = builder.date;
        this.time = builder.time;
        this.status = builder.status;
    }

    public static class GeneralAttendanceBuilder {
        private int rfid;
        private Date date;
        private LocalTime time;
        private String status;

        public GeneralAttendanceBuilder(int rfid) {
            this.rfid = rfid;
        }

        public GeneralAttendanceBuilder date(Date date) {
            this.date = date;
            return this;
        }

        public GeneralAttendanceBuilder time(LocalTime time) {
            this.time = time;
            return this;
        }

        public GeneralAttendanceBuilder status(String status) {
            this.status = status;
            return this;
        }

        public GeneralAttendance build() {
            return new GeneralAttendance(this);
        }
    }


    public int getRfid() { return rfid; }
    public Date getDate() { return date; }
    public LocalTime getTime() { return time; }
    public String getStatus() { return status; }
}