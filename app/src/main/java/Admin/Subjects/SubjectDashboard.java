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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    private void getData() {
        int campusId = getIntent().getIntExtra("campusID", 1);
        String url = "http://193.203.162.232:5050/subject/get_subjects_dashboard?campus_id=" + campusId;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.getBoolean("success")) {
                            JSONArray subjectsArray = response.getJSONArray("subjects");
                            List<SubjectItem> subjectItems = new ArrayList<>();

                            // Add category headers and subject items
                            // Pre Medical Category
                            subjectItems.add(new SubjectItem(SubjectItem.TYPE_HEADER, "Pre Medical", null));

                            // Filter and add pre-medical subjects
                            for (int i = 0; i < subjectsArray.length(); i++) {
                                JSONObject subjectObj = subjectsArray.getJSONObject(i);
                                String subjectName = subjectObj.getString("subject_name");

                                // Check if this is a pre-medical subject
                                if (isPreMedicalSubject(subjectName)) {
                                    String teacherName = subjectObj.getString("teacher_name");
                                    int studentCount = subjectObj.getInt("student_count");
                                    Subject subject = new Subject(subjectName, teacherName, studentCount);
                                    subjectItems.add(new SubjectItem(SubjectItem.TYPE_SUBJECT, null, subject));
                                }
                            }

                            // Computer Category
                            subjectItems.add(new SubjectItem(SubjectItem.TYPE_HEADER, "Computer", null));

                            // Filter and add computer subjects
                            for (int i = 0; i < subjectsArray.length(); i++) {
                                JSONObject subjectObj = subjectsArray.getJSONObject(i);
                                String subjectName = subjectObj.getString("subject_name");

                                // Check if this is a computer subject
                                if (isComputerSubject(subjectName)) {
                                    String teacherName = subjectObj.getString("teacher_name");
                                    int studentCount = subjectObj.getInt("student_count");
                                    Subject subject = new Subject(subjectName, teacherName, studentCount);
                                    subjectItems.add(new SubjectItem(SubjectItem.TYPE_SUBJECT, null, subject));
                                }
                            }

                            subjectsAdapter.setSubjectItems(subjectItems);
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

        // Set the timeout and retry policy
        request.setRetryPolicy(new DefaultRetryPolicy(
                10000, // 10 seconds timeout
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, // Default retry count
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT // Default backoff multiplier
        ));

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    // Helper method to check if a subject belongs to pre-medical category
    private boolean isPreMedicalSubject(String subjectName) {
        String name = subjectName.toLowerCase();
        return name.contains("biology") || name.contains("chemistry");
    }

    // Helper method to check if a subject belongs to computer category
    private boolean isComputerSubject(String subjectName) {
        String name = subjectName.toLowerCase();
        return name.contains("math") || name.contains("ict") ||
                name.contains("computer") || name.contains("programming");
    }
}

// Class to hold both header and subject items
class SubjectItem {
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_SUBJECT = 1;

    private int type;
    private String headerTitle;
    private Subject subject;

    public SubjectItem(int type, String headerTitle, Subject subject) {
        this.type = type;
        this.headerTitle = headerTitle;
        this.subject = subject;
    }

    public int getType() {
        return type;
    }

    public String getHeaderTitle() {
        return headerTitle;
    }

    public Subject getSubject() {
        return subject;
    }
}

class SubjectsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<SubjectItem> subjectItems = new ArrayList<>();

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == SubjectItem.TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.subject_header_item, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.subject_item, parent, false);
            return new SubjectViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        SubjectItem item = subjectItems.get(position);

        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).bind(item.getHeaderTitle());
        } else if (holder instanceof SubjectViewHolder) {
            ((SubjectViewHolder) holder).bind(item.getSubject());
        }
    }

    @Override
    public int getItemViewType(int position) {
        return subjectItems.get(position).getType();
    }

    @Override
    public int getItemCount() {
        return subjectItems.size();
    }

    public void setSubjectItems(List<SubjectItem> subjectItems) {
        this.subjectItems = subjectItems;
        notifyDataSetChanged();
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        private final TextView headerTitleText;

        HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            headerTitleText = itemView.findViewById(R.id.headerTitleText);
        }

        void bind(String title) {
            headerTitleText.setText(title);
        }
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
            subjectNameText.setText(subject.getSubjectName());
            teacherNameText.setText("Teacher: " + subject.getTeacherName());
            studentCountText.setText(subject.getStudentCount() + " Students");
        }
    }
}