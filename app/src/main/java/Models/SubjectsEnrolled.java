package Models;



public class SubjectsEnrolled {
    private int rfid;
    private int subjectId;
    private int subjectAttended;
    private int totalDays;

    private SubjectsEnrolled(SubjectEnrolledBuilder builder) {
        this.rfid = builder.rfid;
        this.subjectId = builder.subjectId;
        this.subjectAttended = builder.subjectAttended;
        this.totalDays = builder.totalDays;
    }

    public static class SubjectEnrolledBuilder {
        private int rfid;
        private int subjectId;
        private int subjectAttended;
        private int totalDays;

        public SubjectEnrolledBuilder(int rfid, int subjectId) {
            this.rfid = rfid;
            this.subjectId = subjectId;
        }

        public SubjectEnrolledBuilder subjectAttended(int subjectAttended) {
            this.subjectAttended = subjectAttended;
            return this;
        }

        public SubjectEnrolledBuilder totalDays(int totalDays) {
            this.totalDays = totalDays;
            return this;
        }

        public SubjectsEnrolled build() {
            return new SubjectsEnrolled(this);
        }
    }


    public int getRfid() { return rfid; }
    public int getSubjectId() { return subjectId; }
    public int getSubjectAttended() { return subjectAttended; }
    public int getTotalDays() { return totalDays; }


    public double getAttendancePercentage() {
        if (totalDays == 0) return 0.0;
        return ((double) subjectAttended / totalDays) * 100;
    }
}