package Admin;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Iterator;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.lms.R;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.List;

public class AdminSingleStudentView extends AppCompatActivity {

    private int studentRFID = 0;
    private RequestQueue requestQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_single_student_view);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Student Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        requestQueue = Volley.newRequestQueue(this);

        TextView personalDetailsText = findViewById(R.id.personalDetailsText);
        ListView subjectsListView = findViewById(R.id.subjectsListView);
        TextView attendanceText = findViewById(R.id.attendanceText);
        TextView resultsText = findViewById(R.id.resultsText);
        TextView finesText = findViewById(R.id.finesText);

        if (getIntent().hasExtra("selectedStudentRfid")) {
            studentRFID = getIntent().getIntExtra("selectedStudentRfid", 0);
        }

        getPersonalDetails(personalDetailsText);
        getSubjectsEnrolled(subjectsListView);
        getAttendanceHistory(attendanceText);
        getResultReports(resultsText);
        getFinesDues(finesText);
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void getPersonalDetails(TextView textView) {
        String url = " http://193.203.162.232:5050/student/details?rfid=" + studentRFID;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        String details = "Name: " + response.getString("student_name") +
                                "\nID: " + response.getInt("RFID") +
                                "\nYear: " + response.getInt("year") +
                                "\nContact: " + response.getString("phone_number");
                        textView.setText(details);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> Toast.makeText(this, "Error fetching details", Toast.LENGTH_SHORT).show());

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
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, subjects);
                    listView.setAdapter(adapter);
                }, error -> Toast.makeText(this, "Error fetching subjects", Toast.LENGTH_SHORT).show());

        request.setRetryPolicy(new DefaultRetryPolicy(
                60000, // 60 seconds timeout
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        requestQueue.add(request);
    }

    private void getAttendanceHistory(TextView textView) {
        String url = "http://193.203.162.232:5050/student/attendance?rfid=" + studentRFID;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        String details = "Total Classes: " + response.getInt("TotalDays") +
                                "\nPresent: " + response.getInt("DaysAttended") +
                                "\nAbsent: " + (response.getInt("TotalDays") - response.getInt("DaysAttended")) +
                                "\nAttendance: " + response.getDouble("AttendancePercentage") + "%";
                        textView.setText(details);
                    } catch (JSONException e) {
                        e.printStackTrace();
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
                            resultText.append(key).append(": ").append(response.getString(key)).append("\n");
                        }

                        textView.setText(resultText.toString().trim());
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
                            textView.setText("No fines due.");
                            return;
                        }

                        StringBuilder finesText = new StringBuilder();
                        Iterator<String> keys = response.keys();
                        while (keys.hasNext()) {
                            String key = keys.next();
                            finesText.append(key).append(": ").append(response.getString(key)).append("\n");
                        }

                        textView.setText(finesText.toString().trim());
                    } catch (JSONException e) {
                        e.printStackTrace();
                        textView.setText("Error parsing fines.");
                    }
                }, error -> textView.setText("Error fetching fines"));

        request.setRetryPolicy(new DefaultRetryPolicy(
                60000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        requestQueue.add(request);
    }


}
