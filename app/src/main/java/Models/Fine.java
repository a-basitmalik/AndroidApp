package Models;

public class Fine {
    private String studentName;
    private String studentId;
    private double amount;
    private boolean isWaived;

    public Fine(String studentName, String studentId, double amount) {
        this.studentName = studentName;
        this.studentId = studentId;
        this.amount = amount;
        this.isWaived = false;
    }


    public String getStudentName() { return studentName; }
    public String getStudentId() { return studentId; }
    public double getAmount() { return amount; }
    public boolean isWaived() { return isWaived; }
    public void setWaived(boolean waived) { isWaived = waived; }
}
