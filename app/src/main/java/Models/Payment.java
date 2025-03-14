package Models;


import java.util.Date;

public class Payment {
    private String studentName;
    private String studentId;
    private double amount;
    private Date paymentDate;
    private String status;

    public Payment(String studentName, String studentId, double amount,
                         Date paymentDate, String status) {
        this.studentName = studentName;
        this.studentId = studentId;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.status = status;
    }


    public String getStudentName() { return studentName; }
    public String getStudentId() { return studentId; }
    public double getAmount() { return amount; }
    public Date getPaymentDate() { return paymentDate; }
    public String getStatus() { return status; }


    public void setStudentName(String studentName) { this.studentName = studentName; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setPaymentDate(Date paymentDate) { this.paymentDate = paymentDate; }
    public void setStatus(String status) { this.status = status; }
}