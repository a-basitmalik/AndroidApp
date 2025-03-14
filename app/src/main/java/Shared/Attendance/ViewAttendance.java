package Shared.Attendance;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.example.lms.R;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Models.StudentAttendance;

public class ViewAttendance extends AppCompatActivity {
    private RecyclerView rvAttendance;
    private TextView tvPresentCount, tvAbsentCount;
    private AttendanceAdapter adapter;
    private List<StudentAttendance> attendanceList;
    private RequestQueue requestQueue;
    private static final String BASE_URL = "http://193.203.162.232:5050/attendance/get_attendance_data_view_attendance?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_attendance);

        rvAttendance = findViewById(R.id.rvAttendance);
        tvPresentCount = findViewById(R.id.tvPresentCount);
        tvAbsentCount = findViewById(R.id.tvAbsentCount);
        FloatingActionButton fabRefresh = findViewById(R.id.fabRefresh);

        rvAttendance.setLayoutManager(new LinearLayoutManager(this));
        attendanceList = new ArrayList<>();
        adapter = new AttendanceAdapter(attendanceList);
        rvAttendance.setAdapter(adapter);

        requestQueue = Volley.newRequestQueue(this);
        fetchAttendanceData();

        fabRefresh.setOnClickListener(v -> fetchAttendanceData());
    }

    private void fetchAttendanceData() {
        int campusId = getIntent().getIntExtra("campusID", 1);
        String url = BASE_URL + "campus_id=" + campusId;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.getBoolean("success")) {
                            attendanceList.clear();
                            JSONArray attendanceArray = response.getJSONArray("attendance_data");

                            for (int i = 0; i < attendanceArray.length(); i++) {
                                JSONObject student = attendanceArray.getJSONObject(i);
                                int rfid = student.getInt("rfid");
                                String name = student.getString("student_name");
                                boolean isPresent = student.getString("status").equals("Present");

                                attendanceList.add(new StudentAttendance(rfid, name, String.valueOf(rfid), isPresent, "Unknown"));
                            }

                            adapter.updateData(attendanceList);
                            tvPresentCount.setText("Present: " + response.getInt("present_count"));
                            tvAbsentCount.setText("Absent: " + response.getInt("absent_count"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                Throwable::printStackTrace
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        // Set custom retry policy with increased timeout
        request.setRetryPolicy(new DefaultRetryPolicy(
                30000, // 30 seconds timeout
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        requestQueue.add(request);
    }

}
