package Shared;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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
    private TextView tvMonthYear;
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

            if ("Monthly".equalsIgnoreCase(type)) {
                fetchAssessmentData();
            } else {
                fetchAssessmentDataElse();

            }
        }
    }


    private void initializeViews() {
        tableAssessment = findViewById(R.id.tableAssessment);
        tvMonthYear = findViewById(R.id.tvMonthYear);
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


                            monthlyAssessments.putIfAbsent(monthYear, new HashMap<>());
                            Map<String, SubjectAssessment> assessmentsMap = monthlyAssessments.get(monthYear);

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
                    }

                    for (String monthYear : monthlyAssessments.keySet()) {
                        addMonthHeader(monthYear);
                        for (SubjectAssessment assessment : monthlyAssessments.get(monthYear).values()) {
                            addSubjectRow(assessment);
                        }
                    }

                } catch (Exception e) {
                    Log.e("Checking", "Error parsing data: " + e.getMessage());
                }
            }
        });
    }

    private void addMonthHeader(String monthYear) {
        TableRow row = new TableRow(this);
        row.setPadding(8, 16, 8, 16);
        row.setBackgroundColor(ContextCompat.getColor(this, R.color.header_background));

        TextView textView = new TextView(this);
        textView.setText(monthYear);
        textView.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
        textView.setGravity(Gravity.CENTER);
        textView.setPadding(8, 16, 8, 16);
        textView.setTextSize(18);
        textView.setTypeface(null, Typeface.BOLD);

        TableRow.LayoutParams params = new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT
        );
        params.span = 7;
        textView.setLayoutParams(params);

        row.addView(textView);
        tableAssessment.addView(row);
    }

    private void addSubjectRow(SubjectAssessment assessment) {
        TableRow row = new TableRow(this);
        row.setPadding(8, 8, 8, 8);
        row.setBackgroundColor(ContextCompat.getColor(this, R.color.table_row_background));

        addCell(row, assessment.getSubject(), 150);
        addCell(row, String.format("%.1f", assessment.getQuiz1()), 80);
        addCell(row, String.format("%.1f", assessment.getQuiz2()), 80);
        addCell(row, String.format("%.1f", assessment.getQuiz3()), 80);
        addCell(row, String.format("%.1f", assessment.getAverageQuizMarks()), 120);
        addCell(row, String.format("%.1f", assessment.getAssessmentTotal()), 120);
        addCell(row, String.format("%.1f", assessment.getAssessmentMarks()), 120);
        addCell(row, String.format("%.1f", assessment.getTotalMarksAchieved()), 120);

        tableAssessment.addView(row);
    }

    private void addCell(TableRow row, String text, int width) {
        TextView textView = new TextView(this);
        TableRow.LayoutParams params = new TableRow.LayoutParams(width, TableRow.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(params);
        textView.setText(text);
        textView.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
        textView.setGravity(Gravity.CENTER);
        textView.setPadding(8, 8, 8, 8);
        textView.setTextAppearance(this, R.style.TableCellStyle);
        row.addView(textView);
    }





    private void fetchAssessmentDataElse() {
        String type=getIntent().getStringExtra("type");
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
                    }
                }
        );

        // Set a custom RetryPolicy to increase timeout
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

                    Map<String, Map<String, SubjectAssessment>> monthlyAssessments = new HashMap<>();

                    // Iterate over dynamic month-year keys in JSON
                    JSONObject assessmentsData = responseData.getJSONObject("assessments"); // Extract the "assessments" object
                    Iterator<String> keys = assessmentsData.keys(); // Iterate over its keys (month-year)

                    while (keys.hasNext()) {
                        String monthYear = keys.next();
                        Log.d("Checking", "Processing Month-Year: " + monthYear);

                        JSONArray assessmentsArray = assessmentsData.getJSONArray(monthYear);

                        Log.d("Checking", "Total Assessments for " + monthYear + ": " + assessmentsArray.length());

                        for (int i = 0; i < assessmentsArray.length(); i++) {
                            JSONObject obj = assessmentsArray.getJSONObject(i);
                            String subjectName = obj.getString("subject_name");

                            double assessmentTotal = obj.isNull("assessment_total") ? 0.0 : obj.optDouble("assessment_total", 0.0);
                            double assessmentMarks = obj.isNull("assessment_marks") ? 0.0 : obj.optDouble("assessment_marks", 0.0);
                            int sequence=obj.getInt("sequence");


                            monthlyAssessments.putIfAbsent(monthYear, new HashMap<>());
                            Map<String, SubjectAssessment> assessmentsMap = monthlyAssessments.get(monthYear);

                            SubjectAssessment subjectAssessment = assessmentsMap.getOrDefault(subjectName,
                                    new SubjectAssessment(subjectName, 0, 0, 0, assessmentMarks, assessmentTotal));



                            assessmentsMap.put(subjectName, subjectAssessment);
                        }
                    }

                    for (String monthYear : monthlyAssessments.keySet()) {
                        addMonthHeaderElse(monthYear);
                        for (SubjectAssessment assessment : monthlyAssessments.get(monthYear).values()) {
                            addSubjectRowElse(assessment);
                        }
                    }

                } catch (Exception e) {
                    Log.e("Checking", "Error parsing data: " + e.getMessage());
                }
            }
        });
    }

    private void addMonthHeaderElse(String monthYear) {
        TableRow row = new TableRow(this);
        row.setPadding(8, 16, 8, 16);
        row.setBackgroundColor(ContextCompat.getColor(this, R.color.header_background));

        TextView textView = new TextView(this);
        textView.setText(monthYear);
        textView.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
        textView.setGravity(Gravity.CENTER);
        textView.setPadding(8, 16, 8, 16);
        textView.setTextSize(18);
        textView.setTypeface(null, Typeface.BOLD);

        TableRow.LayoutParams params = new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT
        );
        params.span = 7;
        textView.setLayoutParams(params);

        row.addView(textView);
        tableAssessment.addView(row);
    }

    private void addSubjectRowElse(SubjectAssessment assessment) {
        TableRow row = new TableRow(this);
        row.setPadding(8, 8, 8, 8);
        row.setBackgroundColor(ContextCompat.getColor(this, R.color.table_row_background));

        addCell(row, assessment.getSubject(), 250);
        addCell(row, String.format("%.1f", assessment.getAssessmentTotal()), 120);
        addCell(row, String.format("%.1f", assessment.getAssessmentMarks()), 120);

        tableAssessment.addView(row);
    }

}
