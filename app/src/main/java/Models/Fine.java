package Models;

public class Fine {
    private String studentName;
    private String studentId;
    private double amount;
    private boolean waived;
    private String type;

    public Fine(String studentName, String studentId, double amount, String type) {
        this.studentName = studentName;
        this.studentId = studentId;
        this.amount = amount;
        this.waived = false;
        this.type = type;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public boolean isWaived() {
        return waived;
    }

    public void setWaived(boolean waived) {
        this.waived = waived;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}