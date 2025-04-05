package Admin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.example.lms.R;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AdminSingleStudentView extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    private int studentRFID = 0;
    private RequestQueue requestQueue;
    private ImageView studentPhoto;
    private String currentPhotoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_single_student_view);

        // Setup toolbar with collapsing behavior
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsingToolbar);
        collapsingToolbar.setTitle("Student Profile");

        requestQueue = Volley.newRequestQueue(this);

        // UI references
        TextView studentNameTitle = findViewById(R.id.studentNameTitle);
        TextView studentPhone = findViewById(R.id.studentPhone);
        TextView studentEmail = findViewById(R.id.studentEmail);
        TextView studentID = findViewById(R.id.studentID);
        TextView studentYear = findViewById(R.id.studentYear);

        ListView subjectsListView = findViewById(R.id.subjectsListView);

        TextView totalClassesText = findViewById(R.id.totalClassesText);
        TextView presentText = findViewById(R.id.presentText);
        TextView absentText = findViewById(R.id.absentText);
        TextView attendancePercentageText = findViewById(R.id.attendancePercentageText);

        TextView resultsText = findViewById(R.id.resultsText);
        TextView finesText = findViewById(R.id.finesText);

        studentPhoto = findViewById(R.id.studentPhoto);
        FloatingActionButton editStudentFab = findViewById(R.id.editStudentFab);

        if (getIntent().hasExtra("selectedStudentRfid")) {
            studentRFID = getIntent().getIntExtra("selectedStudentRfid", 0);
        }

        // Load student data
        loadStudentData(studentNameTitle, studentPhone, studentEmail, studentID, studentYear);
        getSubjectsEnrolled(subjectsListView);
        getAttendanceHistory(totalClassesText, presentText, absentText, attendancePercentageText);
        getResultReports(resultsText);
        getFinesDues(finesText);

        // Set up FAB click listener for photo upload
        editStudentFab.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        });

        // Allow clicking the photo to change it
        studentPhoto.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();

            // Display the selected image
            studentPhoto.setImageURI(imageUri);

            // Upload the image to server
            uploadStudentPhoto(imageUri);
        }
    }

    private void uploadStudentPhoto(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            byte[] imageBytes = getBytes(inputStream);

            String uploadUrl = "http://193.203.162.232:5050/student/upload_photo?rfid=" + studentRFID;

            StringRequest stringRequest = new StringRequest(Request.Method.POST, uploadUrl,
                    response -> {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            if (jsonResponse.getBoolean("success")) {
                                currentPhotoUrl = jsonResponse.getString("photo_url");
                                Toast.makeText(this, "Photo uploaded successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, "Failed to upload photo", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> Toast.makeText(this, "Error uploading photo", Toast.LENGTH_SHORT).show()) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("rfid", String.valueOf(studentRFID));
                    return params;
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    return imageBytes;
                }

                @Override
                public String getBodyContentType() {
                    return "image/jpeg";
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    return super.parseNetworkResponse(response);
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    60000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            ));

            requestQueue.add(stringRequest);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error reading image", Toast.LENGTH_SHORT).show();
        }
    }

    private byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void loadStudentData(TextView nameTitle, TextView phone, TextView email,
                                 TextView id, TextView year) {
        String url = "http://193.203.162.232:5050/student/details?rfid=" + studentRFID;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        // Set student name in title
                        String studentName = response.getString("student_name");
                        nameTitle.setText(studentName);

                        // Set student ID
                        id.setText("Student ID: " + response.getInt("RFID"));

                        // Set student year
                        year.setText("Year: " + response.getInt("year"));

                        // Set contact info with autoLink enabled
                        String phoneNumber = response.getString("phone_number");
                        phone.setText(phoneNumber);

                        // Set email (using a default or from API if available)
                        String emailAddress = response.has("email") ?
                                response.getString("email") :
                                studentName.toLowerCase().replace(" ", ".") + "@university.edu";
                        email.setText(emailAddress);

                        // Load student photo if available
                        if (response.has("photo_url") && !response.isNull("photo_url")) {
                            currentPhotoUrl = response.getString("photo_url");

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing student data", Toast.LENGTH_SHORT).show();
                    }
                }, error -> {
            Toast.makeText(this, "Error fetching student details", Toast.LENGTH_SHORT).show();
        });

        request.setRetryPolicy(new DefaultRetryPolicy(
                60000, // 60 seconds timeout
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        requestQueue.add(request);
    }

    private void getSubjectsEnrolled(ListView listView) {
        String url = "http://193.203.162.232:5050/student/subjects?rfid=" + studentRFID;
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    List<String> subjects = new ArrayList<>();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            subjects.add(response.getString(i));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                            R.layout.subject_list_item, // Create a custom layout
                            R.id.tvSubjectName, // TextView ID in custom layout
                            subjects);
                    listView.setAdapter(adapter);
                }, error -> Toast.makeText(this, "Error fetching subjects", Toast.LENGTH_SHORT).show());

        request.setRetryPolicy(new DefaultRetryPolicy(
                60000, // 60 seconds timeout
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        requestQueue.add(request);
    }

    private void getAttendanceHistory(TextView totalClasses, TextView present,
                                      TextView absent, TextView percentage) {
        String url = "http://193.203.162.232:5050/student/attendance?rfid=" + studentRFID;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        int total = response.getInt("TotalDays");
                        int attended = response.getInt("DaysAttended");
                        int absences = total - attended;
                        double attendancePercentage = response.getDouble("AttendancePercentage");

                        // Update UI with formatted values
                        totalClasses.setText(String.valueOf(total));
                        present.setText(String.valueOf(attended));
                        absent.setText(String.valueOf(absences));
                        percentage.setText(String.format("%.1f%%", attendancePercentage));

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing attendance data", Toast.LENGTH_SHORT).show();
                    }
                }, error -> Toast.makeText(this, "Error fetching attendance", Toast.LENGTH_SHORT).show());

        request.setRetryPolicy(new DefaultRetryPolicy(
                60000, // 60 seconds timeout
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        requestQueue.add(request);
    }

    private void getResultReports(TextView textView) {
        String url = "http://193.203.162.232:5050/student/results?rfid=" + studentRFID;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.length() == 0) {
                            textView.setText("No results available.");
                            return;
                        }

                        StringBuilder resultText = new StringBuilder();
                        Iterator<String> keys = response.keys();

                        while (keys.hasNext()) {
                            String key = keys.next();
                            resultText.append(key).append(": ")
                                    .append(response.getString(key))
                                    .append(keys.hasNext() ? "\n\n" : "");
                        }

                        textView.setText(resultText.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                        textView.setText("Error parsing results.");
                    }
                }, error -> textView.setText("Error fetching results"));

        request.setRetryPolicy(new DefaultRetryPolicy(
                60000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        requestQueue.add(request);
    }

    private void getFinesDues(TextView textView) {
        String url = "http://193.203.162.232:5050/student/fines?rfid=" + studentRFID;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.length() == 0) {
                            textView.setText("No fines or dues pending.");
                            return;
                        }

                        StringBuilder finesText = new StringBuilder();
                        Iterator<String> keys = response.keys();

                        while (keys.hasNext()) {
                            String key = keys.next();
                            finesText.append(key).append(": ")
                                    .append(response.getString(key))
                                    .append(keys.hasNext() ? "\n\n" : "");
                        }

                        textView.setText(finesText.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                        textView.setText("Error parsing fines data.");
                    }
                }, error -> {
            textView.setText("Error fetching fines information.");
            error.printStackTrace();
        });

        request.setRetryPolicy(new DefaultRetryPolicy(
                60000, // 60 seconds timeout
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        requestQueue.add(request);
    }
}