package Shared;
import com.android.volley.DefaultRetryPolicy;
import android.content.Intent;
import android.os.Bundle;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;
import org.json.JSONObject;
import org.json.JSONException;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import java.util.Map;
import java.util.HashMap;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.example.lms.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import Admin.selectCampus;
import Instructor.TeacherDashboard;
import Student.StudentDashboard;

public class shared_login extends AppCompatActivity {
    private AutoCompleteTextView spinnerRole;
    private TextInputLayout tilRole;
    private TextInputLayout tilEmail;
    private TextInputLayout tilPassword;
    private TextInputEditText etEmail;
    private TextInputEditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_shared_login);
        EdgeToEdge.enable(this);

        // Initialize views
        initializeViews();

        initializeViews();
//        setupRoleSpinner();
    }

    private void initializeViews() {
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        tilRole = findViewById(R.id.tilRole);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
//        spinnerRole = findViewById(R.id.spinnerRole);
        MaterialButton btnLogin = findViewById(R.id.btnLogin);
        android.widget.TextView tvForgotPassword = findViewById(R.id.tvForgotPassword);
        android.widget.TextView tvSignUp = findViewById(R.id.tvSignUp);
        btnLogin.setOnClickListener(v -> {
            handleLogin();
        });
    }

//    private void setupRoleSpinner() {
//        String[] roles = new String[]{"Admin", "Teacher", "Student", "Campus Admin"};
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(
//                this,
//                R.layout.dropdown_item,
//                roles
//        );
//        spinnerRole.setAdapter(adapter);
//    }

    private void handleLogin() {
        String id = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (!validateInputs(id, password)) {
            return;
        }

//        Toast.makeText(this, "Login button clicked!", Toast.LENGTH_SHORT).show(); // Debugging
        authenticateUser(id, password);
    }


    private void authenticateUser(String id, String password) {
        String url = "http://193.203.162.232:5050/auth/api/login";  // API endpoint

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("id", id);
            jsonBody.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonBody,
                response -> {

                    try {
                        if (response.has("status") && response.getString("status").equals("success")) {
                            String role = response.getString("role");
                            String userId = response.getString("id");

                            redirectBasedOnRole(role, userId);
                        } else {
                            Toast.makeText(shared_login.this, "Invalid credentials. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(shared_login.this, "Error parsing server response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(shared_login.this, "Login failed! Check your credentials and network connection.", Toast.LENGTH_SHORT).show();
                }) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        // Set custom retry policy to increase timeout (10 seconds)
        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,  // Timeout in milliseconds (10 seconds)
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,  // Number of retries
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT  // Backoff multiplier
        ));

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    private void redirectBasedOnRole(String role, String userId) {
        Intent intent = null;
//        Toast.makeText(this, "Role received: " + role, Toast.LENGTH_LONG).show();

        switch (role.toLowerCase()) {
            case "admin":
                intent = new Intent(shared_login.this, selectCampus.class);
                break;
            case "teacher":
                intent = new Intent(shared_login.this, TeacherDashboard.class);
                break;
            case "student":
                intent = new Intent(shared_login.this, StudentDashboard.class);
                break;
            case "campus_admin":
                intent = new Intent(shared_login.this, selectCampus.class);
                break;
            default:
                Toast.makeText(this, "Unknown user role", Toast.LENGTH_SHORT).show();
                return;
        }

        if (intent != null) {
            intent.putExtra("user_id", userId); // Pass user ID for further use
            startActivity(intent);
            finish();
        }
    }

    private boolean validateInputs(String id, String password) {
        if (id.isEmpty()) {
            etEmail.setError("ID is required");
            etEmail.requestFocus();
            return false;
        }
        if (password.isEmpty()) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return false;
        }
        return true;
    }
}
