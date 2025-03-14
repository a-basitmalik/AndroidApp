package Admin.Subjects;
import com.android.volley.DefaultRetryPolicy;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.lms.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;
import Admin.addSubject;
import Models.Subject;
public class SubjectDashboard extends AppCompatActivity {
    private RecyclerView subjectsRecyclerView;
    private SubjectsAdapter subjectsAdapter;
    private FloatingActionButton addSubjectFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_dashboard);

        initializeViews();
        setupRecyclerView();
        setupFabButton();


        getData();
    }

    private void initializeViews() {
        subjectsRecyclerView = findViewById(R.id.subjectsRecyclerView);
        addSubjectFab = findViewById(R.id.addSubjectFab);
    }

    private void setupRecyclerView() {
        subjectsAdapter = new SubjectsAdapter();
        subjectsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        subjectsRecyclerView.setAdapter(subjectsAdapter);


    }

    private void setupFabButton() {
        addSubjectFab.setOnClickListener(v -> {
            int campusId = getIntent().getIntExtra("campusID", 1);
            Intent i = new Intent(SubjectDashboard.this, addSubject.class);
            i.putExtra("campus_id", campusId); // Pass campus_id in intent
            startActivity(i);
        });
    }


    // :TODO ADD ACTUAL DATA
    private void getData() {
        int campusId = getIntent().getIntExtra("campusID", 1);
        String url = "http://193.203.162.232:5050/subject/get_subjects_dashboard?campus_id=" + campusId;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.getBoolean("success")) {
                            JSONArray subjectsArray = response.getJSONArray("subjects");
                            List<Subject> subjectList = new ArrayList<>();

                            for (int i = 0; i < subjectsArray.length(); i++) {
                                JSONObject subjectObj = subjectsArray.getJSONObject(i);
                                String subjectName = subjectObj.getString("subject_name");
                                String teacherName = subjectObj.getString("teacher_name");
                                int studentCount = subjectObj.getInt("student_count");

                                subjectList.add(new Subject(subjectName, teacherName, studentCount));
                            }

                            subjectsAdapter.setSubjects(subjectList);
                        } else {
                            Toast.makeText(SubjectDashboard.this, "Failed to load subjects", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(SubjectDashboard.this, "Parsing error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(SubjectDashboard.this, "Error retrieving data", Toast.LENGTH_SHORT).show();
                });

        // **Set the timeout and retry policy**
        request.setRetryPolicy(new DefaultRetryPolicy(
                10000, // 10 seconds timeout
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, // Default retry count
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT // Default backoff multiplier
        ));

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }
}

class SubjectsAdapter extends RecyclerView.Adapter<SubjectsAdapter.SubjectViewHolder> {
    private List<Subject> subjects = new ArrayList<>();

    @NonNull
    @Override
    public SubjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.subject_item, parent, false);
        return new SubjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectViewHolder holder, int position) {
        Subject subject = subjects.get(position);
        holder.bind(subject);
    }

    @Override
    public int getItemCount() {
        return subjects.size();
    }

    public void setSubjects(List<Subject> subjects) {
        this.subjects = subjects;
        notifyDataSetChanged();
    }

    static class SubjectViewHolder extends RecyclerView.ViewHolder {
        private final TextView subjectNameText;
        private final TextView teacherNameText;
        private final TextView studentCountText;

        SubjectViewHolder(@NonNull View itemView) {
            super(itemView);
            subjectNameText = itemView.findViewById(R.id.subjectNameText);
            teacherNameText = itemView.findViewById(R.id.teacherNameText);
            studentCountText = itemView.findViewById(R.id.studentCountText);
        }

        void bind(Subject subject) {
            subjectNameText.setText(subject.getSubjectName()); // ✅ Fixed
            teacherNameText.setText("Teacher: " + subject.getTeacherName());
            studentCountText.setText(subject.getStudentCount() + " Students");
        }

    }
}

