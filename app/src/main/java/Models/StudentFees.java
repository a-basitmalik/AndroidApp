package Models;


import java.util.Date;

public class StudentFees {
    private int studentFeesId;
    private int rfid;
    private Date startMonth;
    private Date endMonth;
    private FeeStatus status;

    public enum FeeStatus {
        PAID,
        UNPAID,
        PARTIAL
    }

    private StudentFees(StudentFeesBuilder builder) {
        this.studentFeesId = builder.studentFeesId;
        this.rfid = builder.rfid;
        this.startMonth = builder.startMonth;
        this.endMonth = builder.endMonth;
        this.status = builder.status;
    }

    public static class StudentFeesBuilder {
        private int studentFeesId;
        private int rfid;
        private Date startMonth;
        private Date endMonth;
        private FeeStatus status;

        public StudentFeesBuilder(int rfid) {
            this.rfid = rfid;
        }

        public StudentFeesBuilder studentFeesId(int studentFeesId) {
            this.studentFeesId = studentFeesId;
            return this;
        }

        public StudentFeesBuilder startMonth(Date startMonth) {
            this.startMonth = startMonth;
            return this;
        }

        public StudentFeesBuilder endMonth(Date endMonth) {
            this.endMonth = endMonth;
            return this;
        }

        public StudentFeesBuilder status(FeeStatus status) {
            this.status = status;
            return this;
        }

        public StudentFees build() {
            return new StudentFees(this);
        }
    }


    public int getStudentFeesId() { return studentFeesId; }
    public int getRfid() { return rfid; }
    public Date getStartMonth() { return startMonth; }
    public Date getEndMonth() { return endMonth; }
    public FeeStatus getStatus() { return status; }
}
