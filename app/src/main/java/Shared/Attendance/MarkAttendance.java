package Shared.Attendance;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.lms.R;
import com.google.android.material.button.MaterialButton;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import Models.StudentAttendance;

public class MarkAttendance extends AppCompatActivity {
    private AutoCompleteTextView classSpinner;
    private RecyclerView studentsRecyclerView;
    private MaterialButton saveButton;
    private StudentAttendanceAdapter attendanceAdapter;
    private RequestQueue requestQueue;

    private static final String BASE_URL = "http://193.203.162.232:5050/attendance/get_unmarked_attendees"; // Replace with actual API URL

    void initViews() {
        classSpinner = findViewById(R.id.classSpinner);
        studentsRecyclerView = findViewById(R.id.studentsRecyclerView);
        saveButton = findViewById(R.id.saveButton);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark_attendance);

        initViews();
        requestQueue = Volley.newRequestQueue(this);

        setupClassSpinner();
        setupRecyclerView();
        loadUnmarkedStudentsForClass(0);
        setupSaveButton();
    }

    private void setupClassSpinner() {
        String[] classes = {"First Year", "Second Year"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                classes
        );
        classSpinner.setAdapter(adapter);

        classSpinner.setOnItemClickListener((parent, view, position, id) -> {
            String selectedClass = classes[position];

            int year = 0;

            if (selectedClass.equals("First Year")) {
                year = 1;
            } else if (selectedClass.equals("Second Year")) {
                year = 2;
            }

            loadUnmarkedStudentsForClass(year);
        });
    }

    private void setupRecyclerView() {
        studentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        attendanceAdapter = new StudentAttendanceAdapter(new ArrayList<>());
        studentsRecyclerView.setAdapter(attendanceAdapter);
    }

    private void setupSaveButton() {
        saveButton.setOnClickListener(v -> saveAttendance());
    }

    private void saveAttendance() {
        MarkPresent();

    }

    private void loadUnmarkedStudentsForClass(int year) {
        int campusId = getIntent().getIntExtra("campusID", 1);

        String url = BASE_URL + "?campus_id=" + campusId + "&year=" + year;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        boolean success = response.getBoolean("success");
                        if (success) {
                            JSONArray studentsArray = response.getJSONArray("unmarked_students");
                            List<StudentAttendance> studentList = new ArrayList<>();

                            for (int i = 0; i < studentsArray.length(); i++) {
                                JSONObject studentObj = studentsArray.getJSONObject(i);
                                int rfid = studentObj.getInt("rfid");
                                String name = studentObj.getString("student_name");

                                studentList.add(new StudentAttendance(rfid, name, "", false, ""));
                            }

                            attendanceAdapter = new StudentAttendanceAdapter(studentList);
                            studentsRecyclerView.setAdapter(attendanceAdapter);
                        } else {
                            Toast.makeText(this, "Failed to load students", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Network error: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        );

        request.setRetryPolicy(new DefaultRetryPolicy(
                10 * 1000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        requestQueue.add(request);
    }

    private void MarkPresent() {
        List<StudentAttendance> attendanceList = attendanceAdapter.getAttendanceList();
        JSONArray presentStudentsArray = new JSONArray();

        for (StudentAttendance student : attendanceList) {
            if (student.isPresent()) { // Assuming you have a `isPresent()` method
                presentStudentsArray.put(student.getRfid()); // Only add RFID
            }
        }

        if (presentStudentsArray.length() == 0) {
            Toast.makeText(this, "No students marked as present", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://193.203.162.232:5050/attendance/mark_present";

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("students", presentStudentsArray); // Send only student RFIDs
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST, url, requestBody,
                response -> {
                    try {
                        boolean success = response.getBoolean("success");
                        if (success) {
                            Toast.makeText(this, "Attendance marked successfully!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(this, "Failed to mark attendance", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Network error: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        );

        request.setRetryPolicy(new DefaultRetryPolicy(
                10 * 1000, // 10 seconds timeout
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        requestQueue.add(request);
    }



}
