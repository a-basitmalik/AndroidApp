package Shared.Attendance;
import com.android.volley.DefaultRetryPolicy;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.lms.R;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import org.json.JSONException;
import org.json.JSONObject;

public class AttendanceDashboard extends AppCompatActivity {
    private CircularProgressIndicator presentProgress;
    private CircularProgressIndicator absentProgress;
    private TextView tvPresentCount;
    private TextView tvAbsentCount;
    private LinearLayout btnMarkAttendance;
    private LinearLayout btnViewRecords;
    private int campusId;
    private static final String TAG = "AttendanceDashboard";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_dashboard);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        campusId = getIntent().getIntExtra("campusID", -1);
        initializeViews();
        setupClickListeners();
        fetchAttendanceData();
    }

    private void initializeViews() {
        presentProgress = findViewById(R.id.presentProgress);
        absentProgress = findViewById(R.id.absentProgress);
        tvPresentCount = findViewById(R.id.tvPresentCount);
        tvAbsentCount = findViewById(R.id.tvAbsentCount);
        btnMarkAttendance = findViewById(R.id.btnMarkAttendance);
        btnViewRecords = findViewById(R.id.btnViewRecords);
    }

    private void setupClickListeners() {
        btnMarkAttendance.setOnClickListener(v -> launchMarkAttendance());
        btnViewRecords.setOnClickListener(v -> launchViewRecords());
    }

    private void fetchAttendanceData() {
        String url = "http://193.203.162.232:5050/attendance/get_attendance_data?campus_id=" + campusId;

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getBoolean("success")) {
                                int totalStudents = response.getInt("total_students");
                                int presentStudents = response.getInt("present_students");
                                int absentStudents = response.getInt("absent_students");
                                int presentPercentage = response.getInt("present_percentage");
                                int absentPercentage = response.getInt("absent_percentage");

                                updateAttendanceDisplay(presentStudents, absentStudents, totalStudents, presentPercentage, absentPercentage);
                            } else {
                                Log.e(TAG, "Error: " + response.getString("error"));
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "JSON Parsing Error: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Volley Error: " + error.toString());
                    }
                });


        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000, // Timeout in milliseconds (10 seconds)
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, // Number of retries
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(jsonObjectRequest);
    }

    private void updateAttendanceDisplay(int presentCount, int absentCount,
                                         int totalCount, int presentPercentage,
                                         int absentPercentage) {
        presentProgress.setProgress(presentPercentage);
        absentProgress.setProgress(absentPercentage);

        tvPresentCount.setText(String.format("%d/%d Students", presentCount, totalCount));
        tvAbsentCount.setText(String.format("%d/%d Students", absentCount, totalCount));
    }

    private void launchMarkAttendance() {
        Intent i = new Intent(AttendanceDashboard.this, MarkAttendance.class);
        i.putExtra("campusID", campusId); // Passing campusId
        startActivity(i);
    }

    private void launchViewRecords() {
        Intent i = new Intent(AttendanceDashboard.this, ViewAttendance.class);
        i.putExtra("campusID", campusId); // Passing campusId
        startActivity(i);
    }

}
