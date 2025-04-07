package Shared;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.cardview.widget.CardView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.lms.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import Models.SubjectAssessment;

public class SingleResult extends AppCompatActivity {
    private TableLayout tableAssessment;
    private TextView tvExamTitle;
    private LinearLayout resultContainer;
    private String studentId="1229";
    private static final String API_URL = "http://193.203.162.232:5050/result/get_assessment_monthly";
    private static final String TAG = "SingleResult";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_result);

        initializeViews();

        Intent intent = getIntent();
        if (intent != null) {
            studentId = intent.getStringExtra("STUDENT_ID");
            String type = intent.getStringExtra("type");

            tvExamTitle.setText(type + " Assessment Results");

            if ("Monthly".equalsIgnoreCase(type)) {
                fetchAssessmentData();
            } else {
                fetchAssessmentDataElse();
            }
        }
    }

    private void initializeViews() {
        tableAssessment = findViewById(R.id.tableAssessment);
        tvExamTitle = findViewById(R.id.tvMonthYear); // Reusing the existing TextView but with a more appropriate name
        resultContainer = findViewById(R.id.resultContainer);

        // Apply styles to the title
        tvExamTitle.setTextSize(22);
        tvExamTitle.setTypeface(null, Typeface.BOLD);
        tvExamTitle.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        tvExamTitle.setPadding(0, 24, 0, 24);
    }

    private void fetchAssessmentData() {
        String url = API_URL + "?student_id=" + studentId;
        RequestQueue queue = Volley.newRequestQueue(this);

        Log.d(TAG, "Fetching data from: " + url);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "Response received: " + response.toString());
                        Log.d("JSON_RESPONSE", response.toString());

                        parseAssessmentData(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error fetching data: " + (error.getMessage() != null ? error.getMessage() : "Unknown error"));
                        showErrorMessage("Could not load assessment data. Please try again later.");
                    }
                }
        );

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                20000, // Timeout in milliseconds (20 seconds)
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        queue.add(jsonObjectRequest);
    }

    private void parseAssessmentData(JSONObject responseData) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    tableAssessment.removeAllViews(); // Clear old data before adding new rows

                    Map<String, Map<String, SubjectAssessment>> monthlyAssessments = new HashMap<>();

                    // Iterate over dynamic month-year keys in JSON
                    JSONObject assessmentsData = responseData.getJSONObject("assessments"); // Extract the "assessments" object
                    Iterator<String> keys = assessmentsData.keys(); // Iterate over its keys (month-year)

                    int examNumber = 1; // Start with exam 1

                    while (keys.hasNext()) {
                        String monthYear = keys.next();

                        JSONArray assessmentsArray = assessmentsData.getJSONArray(monthYear);

                        for (int i = 0; i < assessmentsArray.length(); i++) {
                            JSONObject obj = assessmentsArray.getJSONObject(i);
                            String subjectName = obj.getString("subject_name");
                            int quizNumber = obj.optInt("quiz_number", 0);

                            double quizMarks = obj.isNull("quiz_marks") ? 0.0 : obj.optDouble("quiz_marks", 0.0);
                            double assessmentTotal = obj.isNull("assessment_total") ? 0.0 : obj.optDouble("assessment_total", 0.0);
                            double assessmentMarks = obj.isNull("assessment_marks") ? 0.0 : obj.optDouble("assessment_marks", 0.0);

                            // Create a key for the exam number instead of month-year
                            String examKey = "Exam " + examNumber;
                            monthlyAssessments.putIfAbsent(examKey, new HashMap<>());
                            Map<String, SubjectAssessment> assessmentsMap = monthlyAssessments.get(examKey);

                            SubjectAssessment subjectAssessment = assessmentsMap.getOrDefault(subjectName,
                                    new SubjectAssessment(subjectName, 0, 0, 0, assessmentMarks, assessmentTotal));

                            switch (quizNumber) {
                                case 1:
                                    subjectAssessment.setQuiz1(quizMarks);
                                    break;
                                case 2:
                                    subjectAssessment.setQuiz2(quizMarks);
                                    break;
                                case 3:
                                    subjectAssessment.setQuiz3(quizMarks);
                                    break;
                            }

                            double avgQuizMarks = (subjectAssessment.getQuiz1() + subjectAssessment.getQuiz2() + subjectAssessment.getQuiz3()) / 3.0;
                            subjectAssessment.setAverageQuizMarks(avgQuizMarks);

                            double totalMarksAchieved = avgQuizMarks + assessmentMarks;
                            subjectAssessment.setTotalMarksAchieved(totalMarksAchieved);

                            assessmentsMap.put(subjectName, subjectAssessment);
                        }

                        examNumber++; // Increment exam number for the next set
                    }

                    // Add a header row with column titles
                    addTableHeader();

                    for (String examKey : monthlyAssessments.keySet()) {
                        addExamHeader(examKey);
                        for (SubjectAssessment assessment : monthlyAssessments.get(examKey).values()) {
                            addSubjectRow(assessment);
                        }
                    }

                } catch (Exception e) {
                    Log.e("Checking", "Error parsing data: " + e.getMessage());
                    showErrorMessage("Error displaying assessment data.");
                }
            }
        });
    }

    private void addTableHeader() {
        TableRow headerRow = new TableRow(this);
        headerRow.setPadding(8, 16, 8, 16);
        headerRow.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        String[] headers = {"Subject", "Quiz 1", "Quiz 2", "Quiz 3", "Avg Quiz", "Total", "Marks", "Achieved"};
        int[] widths = {150, 80, 80, 80, 120, 120, 120, 120};

        for (int i = 0; i < headers.length; i++) {
            TextView tv = new TextView(this);
            TableRow.LayoutParams params = new TableRow.LayoutParams(widths[i], TableRow.LayoutParams.WRAP_CONTENT);
            tv.setLayoutParams(params);
            tv.setText(headers[i]);
            tv.setTextColor(ContextCompat.getColor(this, R.color.white));
            tv.setGravity(Gravity.CENTER);
            tv.setPadding(8, 8, 8, 8);
            tv.setTypeface(null, Typeface.BOLD);
            headerRow.addView(tv);
        }

        tableAssessment.addView(headerRow);
    }

    private void addExamHeader(String examNumber) {
        CardView cardView = new CardView(this);
        CardView.LayoutParams cardParams = new CardView.LayoutParams(
                CardView.LayoutParams.MATCH_PARENT,
                CardView.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, 24, 0, 0);
        cardView.setLayoutParams(cardParams);
        cardView.setCardElevation(4f);
        cardView.setRadius(8f);
        cardView.setCardBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));

        TextView textView = new TextView(this);
        textView.setText(examNumber);
        textView.setTextColor(ContextCompat.getColor(this, R.color.white));
        textView.setGravity(Gravity.CENTER);
        textView.setPadding(16, 16, 16, 16);
        textView.setTextSize(18);
        textView.setTypeface(null, Typeface.BOLD);

        cardView.addView(textView);

        // Add the card to the table as a full-width row
        TableRow row = new TableRow(this);
        TableRow.LayoutParams params = new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT
        );
        params.span = 8; // Span all columns
        cardView.setLayoutParams(params);
        row.addView(cardView);
        tableAssessment.addView(row);
    }

    private void addSubjectRow(SubjectAssessment assessment) {
        TableRow row = new TableRow(this);
        row.setPadding(8, 8, 8, 8);

        // Alternate row colors for better readability
        boolean isEvenRow = tableAssessment.getChildCount() % 2 == 0;
        int backgroundColor = isEvenRow ?
                ContextCompat.getColor(this, R.color.table_row_even) :
                ContextCompat.getColor(this, R.color.table_row_odd);
        row.setBackgroundColor(backgroundColor);

        // Create a border shape for cells
        GradientDrawable border = new GradientDrawable();
        border.setColor(backgroundColor);
        border.setStroke(1, ContextCompat.getColor(this, R.color.table_border));

        addCell(row, assessment.getSubject(), 150, border);
        addCell(row, String.format("%.1f", assessment.getQuiz1()), 80, border);
        addCell(row, String.format("%.1f", assessment.getQuiz2()), 80, border);
        addCell(row, String.format("%.1f", assessment.getQuiz3()), 80, border);

        // Highlight average quiz marks
        addCell(row, String.format("%.1f", assessment.getAverageQuizMarks()), 120, border);

        addCell(row, String.format("%.1f", assessment.getAssessmentTotal()), 120, border);
        addCell(row, String.format("%.1f", assessment.getAssessmentMarks()), 120, border);

        // Highlight total marks achieved
        GradientDrawable achievedBorder = new GradientDrawable();
        achievedBorder.setColor(getScoreColor(assessment.getTotalMarksAchieved(), assessment.getAssessmentTotal()));
        achievedBorder.setStroke(1, ContextCompat.getColor(this, R.color.table_border));
        addCell(row, String.format("%.1f", assessment.getTotalMarksAchieved()), 120, achievedBorder);

        tableAssessment.addView(row);
    }

    private void addCell(TableRow row, String text, int width, GradientDrawable background) {
        TextView textView = new TextView(this);
        TableRow.LayoutParams params = new TableRow.LayoutParams(width, TableRow.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(params);
        textView.setText(text);
        textView.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
        textView.setGravity(Gravity.CENTER);
        textView.setPadding(8, 12, 8, 12);
        textView.setBackground(background);
        row.addView(textView);
    }

    // Get color based on score percentage
    private int getScoreColor(double score, double total) {
        if (total == 0) return ContextCompat.getColor(this, R.color.table_row_even);

        double percentage = (score / total) * 100;
        if (percentage >= 85) {
            return ContextCompat.getColor(this, R.color.score_excellent);
        } else if (percentage >= 70) {
            return ContextCompat.getColor(this, R.color.score_good);
        } else if (percentage >= 50) {
            return ContextCompat.getColor(this, R.color.score_average);
        } else {
            return ContextCompat.getColor(this, R.color.score_poor);
        }
    }

    private void fetchAssessmentDataElse() {
        String type = getIntent().getStringExtra("type");
        String url = "http://193.203.162.232:5050/result/get_assessment_else"
                + "?student_id=" + studentId
                + "&type=" + type;

        RequestQueue queue = Volley.newRequestQueue(this);

        Log.d(TAG, "Fetching data from: " + url);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "Response received: " + response.toString());
                        Log.d("JSON_RESPONSE", response.toString());

                        parseAssessmentDataElse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error fetching data: " + (error.getMessage() != null ? error.getMessage() : "Unknown error"));
                        showErrorMessage("Could not load assessment data. Please try again later.");
                    }
                }
        );

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                20000, // Timeout in milliseconds (20 seconds)
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        queue.add(jsonObjectRequest);
    }

    private void parseAssessmentDataElse(JSONObject responseData) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    tableAssessment.removeAllViews(); // Clear old data before adding new rows

                    Map<String, Map<String, SubjectAssessment>> assessments = new HashMap<>();

                    // Iterate over dynamic keys in JSON
                    JSONObject assessmentsData = responseData.getJSONObject("assessments");
                    Iterator<String> keys = assessmentsData.keys();

                    int examNumber = 1;

                    while (keys.hasNext()) {
                        String key = keys.next();
                        Log.d("Checking", "Processing Key: " + key);

                        JSONArray assessmentsArray = assessmentsData.getJSONArray(key);

                        Log.d("Checking", "Total Assessments for " + key + ": " + assessmentsArray.length());

                        // Create exam key
                        String examKey = "Exam " + examNumber;

                        for (int i = 0; i < assessmentsArray.length(); i++) {
                            JSONObject obj = assessmentsArray.getJSONObject(i);
                            String subjectName = obj.getString("subject_name");

                            double assessmentTotal = obj.isNull("assessment_total") ? 0.0 : obj.optDouble("assessment_total", 0.0);
                            double assessmentMarks = obj.isNull("assessment_marks") ? 0.0 : obj.optDouble("assessment_marks", 0.0);
                            int sequence = obj.getInt("sequence");

                            assessments.putIfAbsent(examKey, new HashMap<>());
                            Map<String, SubjectAssessment> assessmentsMap = assessments.get(examKey);

                            SubjectAssessment subjectAssessment = assessmentsMap.getOrDefault(subjectName,
                                    new SubjectAssessment(subjectName, 0, 0, 0, assessmentMarks, assessmentTotal));

                            assessmentsMap.put(subjectName, subjectAssessment);
                        }

                        examNumber++;
                    }

                    // Add a header row with column titles
                    addTableHeaderElse();

                    for (String examKey : assessments.keySet()) {
                        addExamHeaderElse(examKey);
                        for (SubjectAssessment assessment : assessments.get(examKey).values()) {
                            addSubjectRowElse(assessment);
                        }
                    }

                } catch (Exception e) {
                    Log.e("Checking", "Error parsing data: " + e.getMessage());
                    showErrorMessage("Error displaying assessment data.");
                }
            }
        });
    }

    private void addTableHeaderElse() {
        TableRow headerRow = new TableRow(this);
        headerRow.setPadding(8, 16, 8, 16);
        headerRow.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        String[] headers = {"Subject", "Total Marks", "Marks Achieved", "Percentage"};
        int[] widths = {250, 120, 120, 120};

        for (int i = 0; i < headers.length; i++) {
            TextView tv = new TextView(this);
            TableRow.LayoutParams params = new TableRow.LayoutParams(widths[i], TableRow.LayoutParams.WRAP_CONTENT);
            tv.setLayoutParams(params);
            tv.setText(headers[i]);
            tv.setTextColor(ContextCompat.getColor(this, R.color.white));
            tv.setGravity(Gravity.CENTER);
            tv.setPadding(8, 8, 8, 8);
            tv.setTypeface(null, Typeface.BOLD);
            headerRow.addView(tv);
        }

        tableAssessment.addView(headerRow);
    }

    private void addExamHeaderElse(String examNumber) {
        CardView cardView = new CardView(this);
        CardView.LayoutParams cardParams = new CardView.LayoutParams(
                CardView.LayoutParams.MATCH_PARENT,
                CardView.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, 24, 0, 0);
        cardView.setLayoutParams(cardParams);
        cardView.setCardElevation(4f);
        cardView.setRadius(8f);
        cardView.setCardBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));

        TextView textView = new TextView(this);
        textView.setText(examNumber);
        textView.setTextColor(ContextCompat.getColor(this, R.color.white));
        textView.setGravity(Gravity.CENTER);
        textView.setPadding(16, 16, 16, 16);
        textView.setTextSize(18);
        textView.setTypeface(null, Typeface.BOLD);

        cardView.addView(textView);

        // Add the card to the table as a full-width row
        TableRow row = new TableRow(this);
        TableRow.LayoutParams params = new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT
        );
        params.span = 4; // Span all columns
        cardView.setLayoutParams(params);
        row.addView(cardView);
        tableAssessment.addView(row);
    }

    private void addSubjectRowElse(SubjectAssessment assessment) {
        TableRow row = new TableRow(this);
        row.setPadding(8, 8, 8, 8);

        // Alternate row colors for better readability
        boolean isEvenRow = tableAssessment.getChildCount() % 2 == 0;
        int backgroundColor = isEvenRow ?
                ContextCompat.getColor(this, R.color.table_row_even) :
                ContextCompat.getColor(this, R.color.table_row_odd);
        row.setBackgroundColor(backgroundColor);

        // Create border shape for cells
        GradientDrawable border = new GradientDrawable();
        border.setColor(backgroundColor);
        border.setStroke(1, ContextCompat.getColor(this, R.color.table_border));

        // Calculate percentage
        double totalMarks = assessment.getAssessmentTotal();
        double marksAchieved = assessment.getAssessmentMarks();
        double percentage = totalMarks > 0 ? (marksAchieved / totalMarks) * 100 : 0;

        // Highlight percentage cell based on performance
        GradientDrawable percentageBorder = new GradientDrawable();
        percentageBorder.setColor(getScoreColor(marksAchieved, totalMarks));
        percentageBorder.setStroke(1, ContextCompat.getColor(this, R.color.table_border));

        addCell(row, assessment.getSubject(), 250, border);
        addCell(row, String.format("%.1f", totalMarks), 120, border);
        addCell(row, String.format("%.1f", marksAchieved), 120, border);
        addCell(row, String.format("%.1f%%", percentage), 120, percentageBorder);

        tableAssessment.addView(row);
    }

    private void showErrorMessage(String message) {
        if (tableAssessment != null) {
            tableAssessment.removeAllViews();

            TextView errorText = new TextView(this);
            errorText.setText(message);
            errorText.setTextColor(ContextCompat.getColor(this, R.color.error_text));
            errorText.setGravity(Gravity.CENTER);
            errorText.setTextSize(16);
            errorText.setPadding(16, 32, 16, 32);

            tableAssessment.addView(errorText);
        }
    }
}