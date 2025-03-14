package Admin;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.lms.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;


public class AdminTeacherView extends AppCompatActivity {
    private TextView teacherName, teacherEmail, teacherPhone;
    private ListView subjectsList, feedbackList;
    private RatingBar teacherRating;

    private ArrayAdapter<String> subjectsAdapter;
    private ArrayAdapter<String> feedbackAdapter;
    private String teacherId;

    private static final String BASE_URL = "http://193.203.162.232:5050/teacher";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_teacher_view);
        if (getIntent().hasExtra("selectedTeacherId")) {
            teacherId = String.valueOf(getIntent().getIntExtra("selectedTeacherId", -1)); // Convert to String
        }

        initializeViews();
        loadTeacherData();
    }


    private void initializeViews() {
        teacherName = findViewById(R.id.teacherName);
        teacherEmail = findViewById(R.id.teacherEmail);
        teacherPhone = findViewById(R.id.teacherPhone);
        subjectsList = findViewById(R.id.subjectsList);
        feedbackList = findViewById(R.id.feedbackList);
        teacherRating = findViewById(R.id.teacherRating);
    }

    private void loadTeacherData() {
        String url = BASE_URL + "/get_teacher/" + teacherId;

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            teacherName.setText(response.getString("name"));
                            teacherEmail.setText(response.getString("email"));
                            teacherPhone.setText(response.getString("phone"));
                            teacherRating.setRating((float) response.getDouble("rating"));

                            // Subjects
                            JSONArray subjectsArray = response.getJSONArray("subjects");
                            List<String> subjects = new ArrayList<>();
                            for (int i = 0; i < subjectsArray.length(); i++) {
                                subjects.add(subjectsArray.getString(i));
                            }
                            subjectsAdapter = new ArrayAdapter<>(AdminTeacherView.this,
                                    android.R.layout.simple_list_item_1, subjects);
                            subjectsList.setAdapter(subjectsAdapter);

                            // Feedback
                            JSONArray feedbackArray = response.getJSONArray("feedback");
                            List<String> feedback = new ArrayList<>();
                            for (int i = 0; i < feedbackArray.length(); i++) {
                                feedback.add(feedbackArray.getString(i));
                            }
                            feedbackAdapter = new ArrayAdapter<>(AdminTeacherView.this,
                                    android.R.layout.simple_list_item_1, feedback);
                            feedbackList.setAdapter(feedbackAdapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(AdminTeacherView.this, "Error parsing data", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley Error", error.toString());
                Toast.makeText(AdminTeacherView.this, "Failed to load teacher data", Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(jsonObjectRequest);
    }

    private void showSubjectDetails(String subject) {
        Intent i = new Intent(AdminTeacherView.this, Shared.SubjectView.class);
        i.putExtra("SubjectName", subject);
        startActivity(i);
    }
}
