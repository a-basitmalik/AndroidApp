package Models;

import java.util.ArrayList;
import java.util.List;

public class SubjectAssessment {
    private String subject;
    private double quiz1;
    private double quiz2;
    private double quiz3;
    private double averageQuizMarks;
    private double assessmentMarks;
    private double assessmentTotal;
    private double totalMarksAchieved;

    public SubjectAssessment(String subject, double quiz1, double quiz2, double quiz3, double assessmentMarks, double assessmentTotal) {
        this.subject = subject;
        this.quiz1 = quiz1;
        this.quiz2 = quiz2;
        this.quiz3 = quiz3;
        this.assessmentMarks = assessmentMarks;
        this.assessmentTotal = assessmentTotal;
        this.averageQuizMarks = 0.0;
        this.totalMarksAchieved = 0.0;
    }

    public String getSubject() { return subject; }
    public double getQuiz1() { return quiz1; }
    public double getQuiz2() { return quiz2; }
    public double getQuiz3() { return quiz3; }
    public double getAverageQuizMarks() { return averageQuizMarks; }
    public double getAssessmentMarks() { return assessmentMarks; }
    public double getAssessmentTotal() { return assessmentTotal; }
    public double getTotalMarksAchieved() { return totalMarksAchieved; }

    public void setQuiz1(double quiz1) { this.quiz1 = quiz1; }
    public void setQuiz2(double quiz2) { this.quiz2 = quiz2; }
    public void setQuiz3(double quiz3) { this.quiz3 = quiz3; }
    public void setAverageQuizMarks(double avgQuizMarks) { this.averageQuizMarks = avgQuizMarks; }
    public void setAssessmentMarks(double assessmentMarks) { this.assessmentMarks = assessmentMarks; }
    public void setAssessmentTotal(double assessmentTotal) { this.assessmentTotal = assessmentTotal; }
    public void setTotalMarksAchieved(double totalMarksAchieved) { this.totalMarksAchieved = totalMarksAchieved; }
}
