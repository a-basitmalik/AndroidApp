package Admin;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.lms.R;

import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Announcement extends AppCompatActivity {

    private static final String API_URL = "http://193.203.162.232:5050/announcement/create";
    private static final String TAG = "CreateAnnouncement";

    private RadioGroup rgAudienceType;
    private RadioButton rbAllStudents, rbSubjectGroups;
    private TextView tvSelectGroups;
    private ChipGroup chipGroupSubjects;
    private Chip chipFA, chipPreMedical, chipPreEngineering, chipICS;
    private TextInputLayout tilSubject, tilAnnouncement;
    private TextInputEditText etSubject, etAnnouncement;
    private MaterialButton btnPostAnnouncement;

    private int campusId;  // Variable to store the campus ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement);

        setSupportActionBar(findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Receive campusId from the Intent
        campusId = getIntent().getIntExtra("campusID", 2);  // Default campus ID is 2 if not found

        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        rgAudienceType = findViewById(R.id.rgAudienceType);
        rbAllStudents = findViewById(R.id.rbAllStudents);
        rbSubjectGroups = findViewById(R.id.rbSubjectGroups);

        tvSelectGroups = findViewById(R.id.tvSelectGroups);
        chipGroupSubjects = findViewById(R.id.chipGroupSubjects);
        chipFA = findViewById(R.id.chipFA);
        chipPreMedical = findViewById(R.id.chipPreMedical);
        chipPreEngineering = findViewById(R.id.chipPreEngineering);
        chipICS = findViewById(R.id.chipICS);

        tilSubject = findViewById(R.id.tilSubject);
        tilAnnouncement = findViewById(R.id.tilAnnouncement);
        etSubject = findViewById(R.id.etSubject);
        etAnnouncement = findViewById(R.id.etAnnouncement);

        btnPostAnnouncement = findViewById(R.id.btnPostAnnouncement);

        rbAllStudents.setChecked(true);
    }

    private void setupListeners() {
        rgAudienceType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbSubjectGroups) {
                tvSelectGroups.setVisibility(View.VISIBLE);
                chipGroupSubjects.setVisibility(View.VISIBLE);
            } else {
                tvSelectGroups.setVisibility(View.GONE);
                chipGroupSubjects.setVisibility(View.GONE);
            }
        });

        btnPostAnnouncement.setOnClickListener(v -> {
            if (validateInputs()) {
                postAnnouncement();
            }
        });
    }

    private boolean validateInputs() {
        boolean isValid = true;

        String subject = etSubject.getText().toString().trim();
        if (subject.isEmpty()) {
            tilSubject.setError("Subject is required");
            isValid = false;
        } else {
            tilSubject.setError(null);
        }

        String announcement = etAnnouncement.getText().toString().trim();
        if (announcement.isEmpty()) {
            tilAnnouncement.setError("Announcement content is required");
            isValid = false;
        } else {
            tilAnnouncement.setError(null);
        }

        if (rbSubjectGroups.isChecked()) {
            boolean atLeastOneGroupSelected = chipFA.isChecked() ||
                    chipPreMedical.isChecked() ||
                    chipPreEngineering.isChecked() ||
                    chipICS.isChecked();

            if (!atLeastOneGroupSelected) {
                Snackbar.make(btnPostAnnouncement, "Please select at least one subject group", Snackbar.LENGTH_SHORT).show();
                isValid = false;
            }
        }

        return isValid;
    }

    private void postAnnouncement() {
        // Get input values
        String subject = etSubject.getText().toString().trim();
        String announcement = etAnnouncement.getText().toString().trim();

        String audienceType = rbAllStudents.isChecked() ? "all" : "group";
        List<String> selectedGroups = new ArrayList<>();

        if (rbSubjectGroups.isChecked()) {
            // Only add groups that are selected
            if (chipFA.isChecked()) selectedGroups.add("FA");
            if (chipPreMedical.isChecked()) selectedGroups.add("PreMedical");
            if (chipPreEngineering.isChecked()) selectedGroups.add("PreEngineering");
            if (chipICS.isChecked()) selectedGroups.add("ICS");
        }

        // Use the campusId received from the Intent
        // String campusId is already set in the onCreate method

        // Prepare the JSON request body
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("subject", subject);
            requestBody.put("announcement", announcement);
            requestBody.put("audience_type", audienceType);

            // Check if any groups are selected
            if (!selectedGroups.isEmpty()) {
                // Convert the selectedGroups list to a JSONArray
                requestBody.put("subject_groups", new JSONArray(selectedGroups));
            } else {
                // If no groups are selected, set "subject_groups" to an empty array or omit it
                requestBody.put("subject_groups", new JSONArray());
            }

            requestBody.put("campus_id", campusId);  // Add campus_id to the request body
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Create a Volley request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, API_URL, requestBody,
                response -> {
                    // Handle success response
                    showSuccessAndFinish("Announcement posted successfully");
                },
                error -> {
                    // Handle error response
                    Snackbar.make(btnPostAnnouncement, "Failed to post announcement", Snackbar.LENGTH_SHORT).show();
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        // Set custom retry policy to increase the timeout
        int timeoutMs = 5000; // Timeout in milliseconds (e.g., 5 seconds)
        int maxRetries = 2;   // Number of retries before failing
        int backoffMultiplier = 2;  // Backoff multiplier to increase the timeout in case of retry
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                timeoutMs,
                maxRetries,
                backoffMultiplier
        ));

        // Add the request to the Volley queue
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    private void showSuccessAndFinish(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

        etSubject.setText("");
        etAnnouncement.setText("");

        btnPostAnnouncement.postDelayed(this::finish, 1500);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
