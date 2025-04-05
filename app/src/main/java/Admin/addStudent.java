package Admin;
import org.json.JSONArray;
import org.json.JSONException;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RetryPolicy;
import android.util.Log;
import android.widget.ImageView;
import android.view.View;
import android.Manifest;
import android.net.Uri;
import android.provider.MediaStore;
import android.graphics.Bitmap;
import android.content.Intent;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.widget.TextView;
import android.util.Base64;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import java.sql.PreparedStatement;
import android.app.AlertDialog;
import android.content.DialogInterface;
import java.util.Random;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONObject;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.cardview.widget.CardView;
import androidx.appcompat.app.AppCompatActivity;


import com.example.lms.R;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

import java.util.List;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import Models.DAO.DatabaseConnection;

import Models.Student;
public class addStudent extends AppCompatActivity {
    private TextInputEditText etStudentName, etPhoneNumber, etRFID, etPassword, etYear, etFeeAmount;
    private Button btnSubmit, btnBulkUpload;
    private AutoCompleteTextView subjectsDropdown;
    private ImageView profileImageView;
    private CardView profileImageCard;
    private TextView uploadPhotoText;
    private List<String> selectedSubjects = new ArrayList<>();
    private int selectedCampusId = 1;
    private int selectedYear = 1;
    private Bitmap profileBitmap = null;
    private static final int PERMISSION_REQUEST_CODE = 100;

    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private ActivityResultLauncher<String[]> filePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);
        selectedCampusId = getIntent().getIntExtra("campusID", 2);

        initializeViews();
        setupImagePicker();
        subjectsDropdown = findViewById(R.id.subjectsDropdown);  // Ensure you have the correct ID here
        getSubjectsForCampusAndYear(selectedCampusId, 1);
        setupValidations();
        setupBulkUpload();
    }

    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImage = result.getData().getData();
                        try {
                            profileBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                            profileImageView.setImageBitmap(profileBitmap);
                            uploadPhotoText.setText("Change Photo");
                        } catch (IOException e) {
                            e.printStackTrace();
                            showErrorDialog("Failed to load image.");
                        }
                    }
                }
        );

        profileImageCard.setOnClickListener(v -> {
            if (checkPermissions()) {
                openImagePicker();
            } else {
                requestPermissions();
            }
        });
    }

    private boolean checkPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                PERMISSION_REQUEST_CODE
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                showErrorDialog("Permission denied. Cannot select profile picture.");
            }
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void setupBulkUpload() {
        filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.OpenDocument(),
                uri -> {
                    if (uri != null) {
                        // Handle CSV file processing here
                        processCsvFile(uri);
                    }
                }
        );

        btnBulkUpload.setOnClickListener(v -> {
            filePickerLauncher.launch(new String[]{"text/csv"});
        });
    }

    private void processCsvFile(Uri fileUri) {
        // CSV file processing logic would go here
        // For now, just show a success message
        showSuccessDialog("CSV file selected successfully. Processing bulk upload.");
    }

    //function added for getting subjects for a campus and year from the database
    private void getSubjectsForCampusAndYear(int campusId, int year) {
        String url = "http://193.203.162.232:5050/subject/get_subjects?campus_id=" + campusId + "&year=" + year;

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading subjects...");
        progressDialog.show();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    progressDialog.dismiss();
                    try {
                        JSONArray subjectsArray = response.getJSONArray("subjects");
                        List<String> subjects = new ArrayList<>();
                        for (int i = 0; i < subjectsArray.length(); i++) {
                            subjects.add(subjectsArray.getString(i));
                        }
                        setupSubjectsDropdown(subjects);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        showErrorDialog("Failed to parse subjects.");
                    }
                },
                error -> {
                    progressDialog.dismiss();
                    showErrorDialog("Error fetching subjects: " + error.getMessage());
                });

        // Increase the timeout by setting a custom RetryPolicy
        int socketTimeout = 30000; // 30 seconds - you can adjust this value
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    private void setupSubjectsDropdown(List<String> subjects) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, subjects);
        subjectsDropdown.setAdapter(adapter);

        subjectsDropdown.setOnItemClickListener((parent, view, position, id) -> {
            String subject = parent.getItemAtPosition(position).toString();
            subjectsDropdown.setHint("Select subjects (click again to remove)");
            if (selectedSubjects.contains(subject)) {
                selectedSubjects.remove(subject);
            } else {
                selectedSubjects.add(subject);
            }
            subjectsDropdown.setText("");
            updateSubjectsDisplay();
        });
    }

    //TODO overlap of error and show password
    private void updateSubjectsDisplay() {
        StringBuilder display = new StringBuilder();
        for (String subject : selectedSubjects) {
            if (display.length() > 0) {
                display.append(", ");
            }
            display.append(subject);
        }
        subjectsDropdown.setHint(display.toString());
    }

    private void initializeViews() {
        etStudentName = findViewById(R.id.etStudentName);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        etRFID = findViewById(R.id.etRFID);
        etPassword = findViewById(R.id.etPassword);
        etYear = findViewById(R.id.etYear);
        etFeeAmount = findViewById(R.id.etFeeAmount);
        subjectsDropdown = findViewById(R.id.subjectsDropdown);
        profileImageView = findViewById(R.id.profileImageView);
        profileImageCard = findViewById(R.id.profileImageCard);
        uploadPhotoText = findViewById(R.id.uploadPhotoText);
        btnSubmit = findViewById(R.id.btnSubmit);

    }

    private void setupValidations() {
        //NAME VALIDATIONS
        etStudentName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() < 2) {
                    etStudentName.setError("Name must be at least 2 characters long");
                } else if (!s.toString().matches("^[a-zA-Z\\s]*$")) {
                    etStudentName.setError("Name should only contain letters");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        //PHONE # VALIDATIONS
        etPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().matches("^[0-9]{10}$")) {
                    etPhoneNumber.setError("Enter a valid 10-digit phone number");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });


        //PASSWORD VALIDATIONS
        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() < 6) {
                    etPassword.setError("Password must be at least 6 characters long");
                } else if (!s.toString().matches(".*[A-Z].*")) {
                    etPassword.setError("Password must contain at least one uppercase letter");
                } else if (!s.toString().matches(".*[0-9].*")) {
                    etPassword.setError("Password must contain at least one number");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        //YEARVALIDATIONS
        etYear.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    int year = Integer.parseInt(s.toString());
                    if (year < 1 || year > 2) { //only 2 years are allowed according to college programs
                        etYear.setError("Year must be between 1 and 2");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnSubmit.setOnClickListener(v -> validateAndSubmit());
    }

    private void validateAndSubmit() {
        if (!validateInputs()) {
            return;
        }

        Student student = new Student.StudentBuilder(
                etStudentName.getText().toString(),
                etPhoneNumber.getText().toString(),
                etPassword.getText().toString())
                .rfid(Integer.parseInt(etRFID.getText().toString()))
                .year(Integer.parseInt(etYear.getText().toString()))
                .campusId(selectedCampusId)
                .studentId(generateStudentID(selectedCampusId))
                .absenteeId(generateAbsenteeID())
                .feeAmount(Integer.parseInt(etFeeAmount.getText().toString()))
                .subjects(selectedSubjects)
                .build();
        Log.d("CampusID", "Selected Campus ID: " + selectedCampusId);
        saveStudent(student);
    }

    private boolean validateInputs() {
        boolean isValid = true;

        if (TextUtils.isEmpty(etStudentName.getText())) {
            etStudentName.setError("Name is required");
            isValid = false;
        }

        if (TextUtils.isEmpty(etPhoneNumber.getText()) ||
                !etPhoneNumber.getText().toString().matches("^[0-9]{10}$")) {
            etPhoneNumber.setError("Valid phone number is required");
            isValid = false;
        }

        if (TextUtils.isEmpty(etRFID.getText())) {
            etRFID.setError("RFID is required");
            isValid = false;
        }

        if (TextUtils.isEmpty(etPassword.getText()) ||
                etPassword.getText().length() < 6) {
            etPassword.setError("Valid password is required");
            isValid = false;
        }

        if (TextUtils.isEmpty(etYear.getText()) ||
                !isValidYear(etYear.getText().toString())) {
            etYear.setError("Valid year is required");
            isValid = false;
        }

        if (TextUtils.isEmpty(etFeeAmount.getText())) {
            etFeeAmount.setError("Fee amount is required");
            isValid = false;
        }

        if (selectedSubjects.isEmpty()) {
            subjectsDropdown.setError("At least one subject is required");
            isValid = false;
        }

        return isValid;
    }

    private boolean isValidYear(String yearStr) {
        try {
            int year = Integer.parseInt(yearStr);
            return year >= 1 && year <= 4;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void showErrorDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error")
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private String encodeImage(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    private void saveStudent(Student student) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Adding student...");
        progressDialog.show();

        String url = "http://193.203.162.232:5050/student/add_student";// Update with your Flask API URL

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("absentee_id", student.getAbsenteeId());
            jsonObject.put("campus_id", student.getCampusId());
            jsonObject.put("fee_amount", student.getFeeAmount());
            jsonObject.put("password", generateRandomPassword());
            jsonObject.put("phone_number", student.getPhoneNumber());
            jsonObject.put("rfid", student.getRfid());
            jsonObject.put("student_id", student.getStudentId());
            jsonObject.put("student_name", student.getStudentName());
            jsonObject.put("year", student.getYear());
            jsonObject.put("subjects", new JSONArray(student.getSubjects()));

            // Add profile image if available
            if (profileBitmap != null) {
                jsonObject.put("profile_image", encodeImage(profileBitmap));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            progressDialog.dismiss();
            showErrorDialog("Error creating JSON request.");
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                response -> {
                    progressDialog.dismiss();
                    showSuccessDialog("Student added successfully!");
                },
                error -> {
                    progressDialog.dismiss();
                    showErrorDialog("Error adding student: " + error.getMessage());
                });

        // Increase timeout to 30 seconds (30000ms)
        int socketTimeout = 30000; // 30 seconds
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    private void showSuccessDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Success")
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private String generateStudentID(int campusId) {
        Connection conn = DatabaseConnection.getConnection();
        if (conn != null) {
            try {
                String query = "SELECT COUNT(*) AS count FROM Students WHERE campusid = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setInt(1, campusId);
                ResultSet rs = stmt.executeQuery();

                int studentNumber = rs.next() ? rs.getInt("count") + 1 : 1;

                rs.close();
                stmt.close();
                conn.close();

                return "LGSC" + campusId + String.format("%03d", studentNumber);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return  campusId + "001";
    }

    //helper function to translate subject names selected in dropdown to their respective ids
    private int getSubjectIdByName(String subjectName, int campusId, int year) {
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return -1;

        try {
            String query = "SELECT subject_id FROM Subjects WHERE subject_name = ? AND CampusID = ? AND year = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, subjectName);
            stmt.setInt(2, campusId);
            stmt.setInt(3, year);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int subjectId = rs.getInt("subject_id");
                rs.close();
                stmt.close();
                conn.close();
                return subjectId;
            }

            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    //I think we should use some password generator instead of taking password as input because admins have to set it
    private String generateRandomPassword() {
        return "LGSC" + new Random().nextInt(9000) + 1000;
    }

    private String generateAbsenteeID() {
        //  DB CALL TODO
        return "";
    }

    private int getCampusId() {
        // DB CALL TODO
        return 0;
    }
}