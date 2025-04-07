package Admin;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import java.util.HashMap;
import java.util.Map;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.RequestQueue;
import com.example.lms.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import Models.Teacher;

public class addSubject extends AppCompatActivity {
    private List<Teacher> teacherList = new ArrayList<>();

    private TextInputEditText etSubjectName, etYear;
    private AutoCompleteTextView teacherDropdown;
    private Button btnSubmit, btnAddTimeSlot;
    private LinearLayout timeSlotContainer;
    private RequestQueue requestQueue;
    private ChipGroup selectedDaysChipGroup;

    private int campusId;
    private String campusName;


    private List<TimeSlot> timeSlots = new ArrayList<>();

    private static final String BASE_URL = "http://193.203.162.232:5050";


    private static class TimeSlot {
        String day;
        String time;

        TimeSlot(String day, String time) {
            this.day = day;
            this.time = time;
        }
    }

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
        setupTeacherDropdown();
        setupAddTimeSlotButton();
        setupSubmitButton();
    }

    private void initializeViews() {
        etSubjectName = findViewById(R.id.etSubjectName);
        etYear = findViewById(R.id.etYear);
        teacherDropdown = findViewById(R.id.teacherDropdown);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnAddTimeSlot = findViewById(R.id.btnAddTimeSlot);
        timeSlotContainer = findViewById(R.id.timeSlotContainer);
        selectedDaysChipGroup = findViewById(R.id.selectedDaysChipGroup);
    }

    private void setupTeacherDropdown() {

        loadTeachers();
    }

    private void setupAddTimeSlotButton() {
        btnAddTimeSlot.setOnClickListener(v -> showAddTimeSlotDialog());
    }

    private void showAddTimeSlotDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_time_slot, null);

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Add Time Slot");
        builder.setView(dialogView);

        final ChipGroup dayChipGroup = dialogView.findViewById(R.id.chipGroupDays);
        final TextInputEditText etTimeDialog = dialogView.findViewById(R.id.etTimeDialog);


        etTimeDialog.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    this,
                    (view, hourOfDay, minute) -> {
                        String time = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                        etTimeDialog.setText(time);
                    },
                    Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                    Calendar.getInstance().get(Calendar.MINUTE),
                    true
            );
            timePickerDialog.show();
        });

        builder.setPositiveButton("Add", (dialog, which) -> {
            String time = etTimeDialog.getText().toString();
            if (TextUtils.isEmpty(time)) {
                Toast.makeText(this, "Please select a time", Toast.LENGTH_SHORT).show();
                return;
            }

            List<String> selectedDays = new ArrayList<>();
            for (int i = 0; i < dayChipGroup.getChildCount(); i++) {
                Chip chip = (Chip) dayChipGroup.getChildAt(i);
                if (chip.isChecked()) {
                    selectedDays.add(chip.getText().toString());
                }
            }

            if (selectedDays.isEmpty()) {
                Toast.makeText(this, "Please select at least one day", Toast.LENGTH_SHORT).show();
                return;
            }


            for (String day : selectedDays) {
                addTimeSlot(day, time);
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void addTimeSlot(String day, String time) {

        timeSlots.add(new TimeSlot(day, time));


        Chip chip = new Chip(this);
        chip.setText(day + " at " + time);
        chip.setCloseIconVisible(true);
        chip.setCheckable(false);

        chip.setOnCloseIconClickListener(v -> {

            for (int i = 0; i < timeSlots.size(); i++) {
                TimeSlot slot = timeSlots.get(i);
                if (slot.day.equals(day) && slot.time.equals(time)) {
                    timeSlots.remove(i);
                    break;
                }
            }

            selectedDaysChipGroup.removeView(chip);
        });

        selectedDaysChipGroup.addView(chip);
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
        if (TextUtils.isEmpty(teacherDropdown.getText())) {
            teacherDropdown.setError("Teacher is required");
            isValid = false;
        }
        if (TextUtils.isEmpty(etYear.getText())) {
            etYear.setError("Year is required");
            isValid = false;
        }
        if (timeSlots.isEmpty()) {
            Toast.makeText(this, "At least one day and time slot is required", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        return isValid;
    }

    private void saveSubject() {
        String url = BASE_URL + "/subject/api/add_subject";

        String subjectName = etSubjectName.getText().toString();
        int year = Integer.parseInt(etYear.getText().toString());


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
            // Create a JSON array of time slots
            JSONArray timeSlotArray = new JSONArray();
            for (TimeSlot slot : timeSlots) {
                JSONObject slotObj = new JSONObject();
                slotObj.put("day", slot.day);
                slotObj.put("time", slot.time);
                timeSlotArray.put(slotObj);
            }

            JSONObject jsonBody = new JSONObject();
            jsonBody.put("subject_name", subjectName);
            jsonBody.put("time_slots", timeSlotArray);
            jsonBody.put("teacher_id", teacherId);
            jsonBody.put("teacher_name", selectedTeacherName);
            jsonBody.put("campus_id", campusId);
            jsonBody.put("campus_name", campusName);
            jsonBody.put("year", year);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                    response -> {
                        Toast.makeText(this, "Subject added successfully!", Toast.LENGTH_SHORT).show();
                        finish(); // Return to previous screen after successful addition
                    },
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