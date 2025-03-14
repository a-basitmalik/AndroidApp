package Admin;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import java.util.HashMap;
import java.util.Map;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.RequestQueue;
import com.example.lms.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import Models.Teacher;

public class addSubject extends AppCompatActivity {
    private List<Teacher> teacherList = new ArrayList<>();

    private TextInputEditText etSubjectName, etTime, etYear;
    private AutoCompleteTextView dayDropdown, teacherDropdown;
    private Button btnSubmit;
    private RequestQueue requestQueue;

    private int campusId;
    private String campusName;

    private static final String BASE_URL = "http://193.203.162.232:5050"; // Replace with your Flask API URL

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_subject);

        requestQueue = Volley.newRequestQueue(this);

        campusId = getIntent().getIntExtra("campus_id", 1);

        if (campusId == -1) {
            Toast.makeText(this, "Invalid campus data!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initializeViews();
        setupDropdowns();
        setupTimePicker();
        setupSubmitButton();
    }

    private void initializeViews() {
        etSubjectName = findViewById(R.id.etSubjectName);
        etTime = findViewById(R.id.etTime);
        etYear = findViewById(R.id.etYear);
        dayDropdown = findViewById(R.id.dayDropdown);
        teacherDropdown = findViewById(R.id.teacherDropdown);
        btnSubmit = findViewById(R.id.btnSubmit);
    }

    private void setupDropdowns() {
        // Static days dropdown
        String[] days = new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        ArrayAdapter<String> daysAdapter = new ArrayAdapter<>(this, R.layout.dropdown_item, days);
        dayDropdown.setAdapter(daysAdapter);

        // Load teachers from API
        loadTeachers();
    }

    private void setupTimePicker() {
        etTime.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    this,
                    (view, hourOfDay, minute) -> {
                        String time = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                        etTime.setText(time);
                    },
                    Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                    Calendar.getInstance().get(Calendar.MINUTE),
                    true
            );
            timePickerDialog.show();
        });
    }

    private void loadTeachers() {
        String url = BASE_URL + "/subject/api/teachers";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    teacherList.clear();
                    List<String> teacherNames = new ArrayList<>();
                    Gson gson = new Gson();
                    teacherList = gson.fromJson(response.toString(), new TypeToken<List<Teacher>>() {}.getType());

                    for (Teacher teacher : teacherList) {
                        teacherNames.add(teacher.getName());
                    }

                    ArrayAdapter<String> teacherAdapter = new ArrayAdapter<>(this, R.layout.dropdown_item, teacherNames);
                    teacherDropdown.setAdapter(teacherAdapter);
                },
                error -> Toast.makeText(this, "Error loading teachers!", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        // Increase timeout settings
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000, // 15 seconds timeout
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        requestQueue.add(jsonArrayRequest);
    }

    private void setupSubmitButton() {
        btnSubmit.setOnClickListener(v -> {
            if (validateInputs()) {
                saveSubject();
            }
        });
    }

    private boolean validateInputs() {
        boolean isValid = true;

        if (TextUtils.isEmpty(etSubjectName.getText())) {
            etSubjectName.setError("Subject name is required");
            isValid = false;
        }
        if (TextUtils.isEmpty(dayDropdown.getText())) {
            dayDropdown.setError("Day is required");
            isValid = false;
        }
        if (TextUtils.isEmpty(etTime.getText())) {
            etTime.setError("Time is required");
            isValid = false;
        }
        if (TextUtils.isEmpty(teacherDropdown.getText())) {
            teacherDropdown.setError("Teacher is required");
            isValid = false;
        }
        if (TextUtils.isEmpty(etYear.getText())) {
            etYear.setError("Year is required");
            isValid = false;
        }

        return isValid;
    }

    private void saveSubject() {
        String url = BASE_URL + "/subject/api/add_subject";

        String subjectName = etSubjectName.getText().toString();
        String day = dayDropdown.getText().toString();
        String time = etTime.getText().toString();
        int year = Integer.parseInt(etYear.getText().toString());

        // Find selected teacher
        String selectedTeacherName = teacherDropdown.getText().toString();

        int teacherId = -1;
        for (Teacher t : teacherList) {
            if (t.getName().equals(selectedTeacherName)) {
                teacherId = t.getId();
                break;
            }
        }

        if (teacherId == -1) {
            Toast.makeText(this, "Invalid teacher!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("subject_name", subjectName);
            jsonBody.put("day", day);
            jsonBody.put("time", time);
            jsonBody.put("teacher_id", teacherId);
            jsonBody.put("teacher_name", selectedTeacherName);
            jsonBody.put("campus_id", campusId); // Use campus ID from intent
            jsonBody.put("campus_name", campusName); // Use campus name from intent
            jsonBody.put("year", year);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                    response -> Toast.makeText(this, "Subject added successfully!", Toast.LENGTH_SHORT).show(),
                    error -> Toast.makeText(this, "Error saving subject: " + error.getMessage(), Toast.LENGTH_SHORT).show()
            ) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";  // Ensure JSON content type
                }
            };

            // Set custom timeout (10 seconds)
            request.setRetryPolicy(new DefaultRetryPolicy(
                    10000, // Timeout in milliseconds (10 seconds)
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES, // Retry count
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT // Backoff multiplier
            ));

            requestQueue.add(request);

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "JSON Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
