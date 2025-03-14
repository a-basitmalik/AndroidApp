package Admin;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.widget.Toast;
import com.example.lms.R;
import com.google.android.material.textfield.TextInputEditText;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class AddTeacher extends AppCompatActivity {
    private TextInputEditText etTeacherName, etPassword, etPhoneNumber;
    private Button btnSubmit;
    private int selectedCampusId;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_teacher);

        requestQueue = Volley.newRequestQueue(this);
        selectedCampusId = getIntent().getIntExtra("campusID", 1);

        initializeViews();
    }

    private void initializeViews() {
        etTeacherName = findViewById(R.id.etTeacherName);
        etPassword = findViewById(R.id.etPassword);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        btnSubmit = findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(v -> validateAndSubmit());
    }

    private boolean validateFields() {
        boolean isValid = true;
        StringBuilder errorMessage = new StringBuilder();

        if (etTeacherName.getText().toString().trim().isEmpty()) {
            errorMessage.append("Teacher name is required.\n");
            isValid = false;
        }
        if (etPhoneNumber.getText().toString().trim().isEmpty()) {
            errorMessage.append("Phone number is required.\n");
            isValid = false;
        }
        if (etPassword.getText().toString().trim().length() < 6) {
            errorMessage.append("Password must be at least 6 characters long.\n");
            isValid = false;
        }

        if (!isValid) {
            Toast.makeText(this, errorMessage.toString().trim(), Toast.LENGTH_LONG).show();
        }
        return isValid;
    }

    private void validateAndSubmit() {
        if (!validateFields()) return;

        String url = "http://193.203.162.232:5050/teacher/register_teacher";
        Map<String, String> params = new HashMap<>();
        params.put("name", etTeacherName.getText().toString().trim());
        params.put("password", etPassword.getText().toString().trim());
        params.put("phone", etPhoneNumber.getText().toString().trim());
        params.put("campus_id", String.valueOf(selectedCampusId));
        params.put("email", etTeacherName.getText().toString().trim().toLowerCase().replace(" ", "") + "@lgscolleges.edu.pk");

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
                response -> Toast.makeText(this, "Teacher registered successfully!", Toast.LENGTH_SHORT).show(),
                error -> Toast.makeText(this, "Failed to register teacher", Toast.LENGTH_SHORT).show()) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);
    }
}
