package Models;


import java.util.ArrayList;
import java.util.List;


public class MonthlyAssessment {
    private String monthYear;
    private List<SubjectAssessment> assessments;

    public MonthlyAssessment(String monthYear) {
        this.monthYear = monthYear;
        this.assessments = new ArrayList<>();
    }

    public void addAssessment(SubjectAssessment assessment) {
        assessments.add(assessment);
    }

    public String getMonthYear() { return monthYear; }
    public List<SubjectAssessment> getAssessments() { return assessments; }
}