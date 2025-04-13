package Shared.Attendance;
import com.android.volley.DefaultRetryPolicy;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.lms.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import org.json.JSONException;
import org.json.JSONObject;

public class AttendanceDashboard extends AppCompatActivity {
    // Student views
    private CircularProgressIndicator studentPresentProgress;
    private CircularProgressIndicator studentOnLeaveProgress;
    private CircularProgressIndicator studentAbsentProgress;
    private TextView tvStudentPresentCount;
    private TextView tvStudentOnLeaveCount;
    private TextView tvStudentAbsentCount;

    // Teacher views
    private CircularProgressIndicator teacherPresentProgress;
    private CircularProgressIndicator teacherOnLeaveProgress;
    private CircularProgressIndicator teacherAbsentProgress;
    private TextView tvTeacherPresentCount;
    private TextView tvTeacherOnLeaveCount;
    private TextView tvTeacherAbsentCount;

    // User type selection
    private RadioGroup userTypeRadioGroup;
    private RadioButton rbStudent;
    private RadioButton rbTeacher;
    private String userType = "Student"; // Default user type

    // Action buttons
    private LinearLayout btnMarkAttendance;
    private LinearLayout btnViewRecords;
    private LinearLayout btnEditAttendance;
    private MaterialButton btnSummary;

    private int campusId;
    private static final String TAG = "AttendanceDashboard";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_dashboard);

        campusId = getIntent().getIntExtra("campusID", -1);
        initializeViews();
        setupClickListeners();
        fetchAttendanceData();
    }

    private void initializeViews() {
        // User type selection
        userTypeRadioGroup = findViewById(R.id.userTypeRadioGroup);
        rbStudent = findViewById(R.id.rbStudent);
        rbTeacher = findViewById(R.id.rbTeacher);

        // Student views
        studentPresentProgress = findViewById(R.id.studentPresentProgress);
        studentOnLeaveProgress = findViewById(R.id.studentOnLeaveProgress);
        studentAbsentProgress = findViewById(R.id.studentAbsentProgress);
        tvStudentPresentCount = findViewById(R.id.tvStudentPresentCount);
        tvStudentOnLeaveCount = findViewById(R.id.tvStudentOnLeaveCount);
        tvStudentAbsentCount = findViewById(R.id.tvStudentAbsentCount);

        // Teacher views
        teacherPresentProgress = findViewById(R.id.teacherPresentProgress);
        teacherOnLeaveProgress = findViewById(R.id.teacherOnLeaveProgress);
        teacherAbsentProgress = findViewById(R.id.teacherAbsentProgress);
        tvTeacherPresentCount = findViewById(R.id.tvTeacherPresentCount);
        tvTeacherOnLeaveCount = findViewById(R.id.tvTeacherOnLeaveCount);
        tvTeacherAbsentCount = findViewById(R.id.tvTeacherAbsentCount);

        // Action buttons
        btnMarkAttendance = findViewById(R.id.btnMarkAttendance);
        btnViewRecords = findViewById(R.id.btnViewRecords);
        btnEditAttendance = findViewById(R.id.btnEditAttendance);
        btnSummary = findViewById(R.id.btnSummary);
    }

    private void setupClickListeners() {
        // Set up user type selection listener
        userTypeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbStudent) {
                userType = "Student";
            } else if (checkedId == R.id.rbTeacher) {
                userType = "Teacher";
            }
            updateUIBasedOnUserType();
        });

        btnMarkAttendance.setOnClickListener(v -> handleMarkAttendance());
        btnViewRecords.setOnClickListener(v -> launchViewRecords());
        btnEditAttendance.setOnClickListener(v -> launchEditAttendance());
        btnSummary.setOnClickListener(v -> printAttendanceSummary());
    }

    private void updateUIBasedOnUserType() {
        // Update UI elements based on selected user type
        if (userType.equals("Student")) {
            // Show UI elements relevant for marking student attendance
            // Optionally hide teacher-specific features if needed
            btnEditAttendance.setVisibility(View.GONE); // Hide edit attendance for students
        } else {
            // Show UI elements relevant for marking teacher attendance
            btnEditAttendance.setVisibility(View.VISIBLE); // Show for teachers
        }

        // You can customize which statistics are shown based on user type
        // For example, a student might only see their own attendance
        // while a teacher might see all attendance statistics
    }

    private void handleMarkAttendance() {
        if (userType.equals("Student")) {
            launchMarkStudentAttendance();
        } else {
            launchMarkTeacherAttendance();
        }
    }

    private void showAttendanceTypeDialog() {
        String[] options = {"Mark Student Attendance", "Mark Teacher Attendance"};
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Select Attendance Type");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                launchMarkStudentAttendance();
            } else {
                launchMarkTeacherAttendance();
            }
        });
        builder.show();
    }

    private void fetchAttendanceData() {
        String url = "http://193.203.162.232:5050/attendance/get_attendance?campusId=" + campusId;

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.getBoolean("success")) {
                            // Student data
                            JSONObject studentData = response.getJSONObject("student_data");
                            int totalStudents = studentData.getInt("total_students");
                            int presentStudents = studentData.getInt("present_students");
                            int onLeaveStudents = studentData.getInt("on_leave_students");
                            int absentStudents = studentData.getInt("absent_students");
                            int presentStudentPercentage = studentData.getInt("present_percentage");
                            int onLeaveStudentPercentage = studentData.getInt("on_leave_percentage");
                            int absentStudentPercentage = studentData.getInt("absent_percentage");

                            // Teacher data
                            JSONObject teacherData = response.getJSONObject("teacher_data");
                            int totalTeachers = teacherData.getInt("total_teachers");
                            int presentTeachers = teacherData.getInt("present_teachers");
                            int onLeaveTeachers = teacherData.getInt("on_leave_teachers");
                            int absentTeachers = teacherData.getInt("absent_teachers");
                            int presentTeacherPercentage = teacherData.getInt("present_percentage");
                            int onLeaveTeacherPercentage = teacherData.getInt("on_leave_percentage");
                            int absentTeacherPercentage = teacherData.getInt("absent_percentage");

                            updateStudentAttendanceDisplay(presentStudents, onLeaveStudents, absentStudents,
                                    totalStudents, presentStudentPercentage, onLeaveStudentPercentage, absentStudentPercentage);

                            updateTeacherAttendanceDisplay(presentTeachers, onLeaveTeachers,
                                    absentTeachers, totalTeachers, presentTeacherPercentage,
                                    onLeaveTeacherPercentage, absentTeacherPercentage);

                            // Initialize UI based on default user type after data is loaded
                            updateUIBasedOnUserType();
                        } else {
                            Log.e(TAG, "Error: " + response.getString("error"));
                            Toast.makeText(this, "Failed to load attendance data", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON Parsing Error: " + e.getMessage());
                        Toast.makeText(this, "Data parsing error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e(TAG, "Volley Error: " + error.toString());
                    Toast.makeText(this, "Network error occurred", Toast.LENGTH_SHORT).show();
                });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(jsonObjectRequest);
    }

    private void updateStudentAttendanceDisplay(int presentCount, int onLeaveCount, int absentCount,
                                                int totalCount, int presentPercentage,
                                                int onLeavePercentage, int absentPercentage) {
        studentPresentProgress.setProgress(presentPercentage);
        studentOnLeaveProgress.setProgress(onLeavePercentage);
        studentAbsentProgress.setProgress(absentPercentage);

        tvStudentPresentCount.setText(String.format("%d/%d Students", presentCount, totalCount));
        tvStudentOnLeaveCount.setText(String.format("%d/%d Students", onLeaveCount, totalCount));
        tvStudentAbsentCount.setText(String.format("%d/%d Students", absentCount, totalCount));
    }

    private void updateTeacherAttendanceDisplay(int presentCount, int onLeaveCount, int absentCount,
                                                int totalCount, int presentPercentage,
                                                int onLeavePercentage, int absentPercentage) {
        teacherPresentProgress.setProgress(presentPercentage);
        teacherOnLeaveProgress.setProgress(onLeavePercentage);
        teacherAbsentProgress.setProgress(absentPercentage);

        tvTeacherPresentCount.setText(String.format("%d/%d Teachers", presentCount, totalCount));
        tvTeacherOnLeaveCount.setText(String.format("%d/%d Teachers", onLeaveCount, totalCount));
        tvTeacherAbsentCount.setText(String.format("%d/%d Teachers", absentCount, totalCount));
    }

    private void launchMarkStudentAttendance() {
        Intent i = new Intent(AttendanceDashboard.this, MarkAttendance.class);
        i.putExtra("campusID", campusId);
        i.putExtra("type", "Student");
        startActivity(i);
    }

    private void launchMarkTeacherAttendance() {
        Intent i = new Intent(AttendanceDashboard.this, MarkAttendance.class);
        i.putExtra("campusID", campusId);
        i.putExtra("type", "Teacher");
        startActivity(i);
    }

    private void launchViewRecords() {
        Intent i = new Intent(AttendanceDashboard.this, ViewAttendance.class);
        i.putExtra("campusID", campusId);
        i.putExtra("userType", userType);  // Pass the selected user type
        startActivity(i);
    }

    private void launchEditAttendance() {
        Intent i = new Intent(AttendanceDashboard.this, EditAttendance.class);
        i.putExtra("campusID", campusId);
        i.putExtra("userType", userType);  // Pass the selected user type
        startActivity(i);
    }

    private void printAttendanceSummary() {
        // Implement your printing logic here
        Toast.makeText(this, "Generating " + userType + " attendance summary...", Toast.LENGTH_SHORT).show();

        // For actual implementation, you might want to:
        // 1. Generate a PDF report
        // 2. Share the report via email or other apps
        // 3. Connect to a printer if available

        // Example:
        // generatePdfReport(userType);
        // shareReport();
    }
}