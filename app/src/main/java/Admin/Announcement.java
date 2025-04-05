package Admin;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.lms.R;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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

    private static final String API_URL = "http://example.com/api/announcements/create";
    private static final String TAG = "CreateAnnouncement";


    private RadioGroup rgAudienceType;
    private RadioButton rbAllStudents, rbSubjectGroups;
    private TextView tvSelectGroups;
    private ChipGroup chipGroupSubjects;
    private Chip chipFA, chipPreMedical, chipPreEngineering, chipICS;
    private TextInputLayout tilSubject, tilAnnouncement;
    private TextInputEditText etSubject, etAnnouncement;
    private MaterialButton btnPostAnnouncement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement);


        setSupportActionBar(findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


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

    }

    private void sendAnnouncementRequest(JSONObject requestBody) {

        showSuccessAndFinish("");
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