package Shared;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.lms.R;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import org.json.JSONException;
import java.util.ArrayList;
import Admin.AddTeacher;
import Admin.AdminSingleStudentView;
import Admin.AdminTeacherView;
import Admin.AlumniProfileView;
import Admin.addStudent;

public class SharedList extends AppCompatActivity {
    private ArrayAdapter<String> adapter;
    private ArrayList<String> namesList;
    private ArrayList<Integer> idsList;
    private static final String TYPE_STUDENTS = "students";
    private static final String TYPE_ALUMNI = "alumni";
    private static final String TYPE_TEACHERS = "teachers";

    private String type;
    private ExtendedFloatingActionButton addSubjectFab;
    TextView header;
    private RequestQueue requestQueue;
    private static final String BASE_URL = "http://193.203.162.232:5050/shared";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared_list);

        type = getIntent().getStringExtra("type");
        addSubjectFab = findViewById(R.id.addSubjectFab);
        header = findViewById(R.id.header);
        setupWindowInsets();

        requestQueue = Volley.newRequestQueue(this);
        namesList = new ArrayList<>();
        idsList = new ArrayList<>();
        int campusID =  getIntent().getIntExtra("campusID", 1);
        initializeList(campusID);
        setupFabButton();

    }

    private void setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initializeList(int campusID) {
        if (TYPE_STUDENTS.equals(type)) {
            header.setText("Student's Dashboard");
            fetchStudentData(campusID);
        } else if (TYPE_TEACHERS.equals(type)) {
            header.setText("Teacher's Dashboard");
            fetchTeacherData(campusID);
        }
        else if (TYPE_ALUMNI.equals(type)) {
            header.setText("Alumni's Dashboard");
            fetchTeacherData(campusID);
        }
        else {
                Toast.makeText(this, "Invalid Type", Toast.LENGTH_SHORT).show();
            }
    }

    private void setupFabButton() {
        addSubjectFab.setOnClickListener(v -> {
            Intent i;
            int campusID = getIntent().getIntExtra("campusID", 1); // Retrieve campusID properly
            if (TYPE_STUDENTS.equals(type)) {
                i = new Intent(SharedList.this, addStudent.class);
            } else if (TYPE_TEACHERS.equals(type)) {
                i = new Intent(SharedList.this, AddTeacher.class);
            }
            else {
                return;
            }
            i.putExtra("campusID", campusID); // Pass campusID to the new activity
            startActivity(i);
        });
    }
    private void fetchStudentData(int campusID) {
        fetchList(BASE_URL + "/get_students?campusID=" + campusID, "student_name", "rfid");
    }

    private void fetchTeacherData(int campusID) {
        fetchList(BASE_URL + "/get_teachers?campusID=" + campusID, "name", "teacher_id");
    }
    private void fetchList(String url, String nameKey, String idKey) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET, url, null,
                response -> {
                    namesList.clear();
                    idsList.clear();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            String name = response.getJSONObject(i).getString(nameKey);
                            int id = response.getJSONObject(i).getInt(idKey);
                            namesList.add(name);
                            idsList.add(id);
                        }
                        loadList();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("Volley", "Error: " + error.getMessage());
                    Toast.makeText(this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
                });

        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        requestQueue.add(jsonArrayRequest);
    }

    private void loadList() {
        ListView listView = findViewById(R.id.studentListView);
        TextInputEditText searchEditText = findViewById(R.id.searchEditText);
        adapter = new ArrayAdapter<>(
                this, R.layout.lightlist, android.R.id.text1, namesList
        );

        listView.setAdapter(adapter);
        setupSearchListener(searchEditText);
        setupItemClickListener(listView);
    }

    private void setupSearchListener(TextInputEditText searchEditText) {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                filterItems(s.toString());
            }
        });
    }

    private void setupItemClickListener(ListView listView) {
        listView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedName = namesList.get(position);
            int selectedId = idsList.get(position);
            int campusID = getIntent().getIntExtra("campusID", 1); // Retrieve campusID

            Intent intent;
            if (TYPE_STUDENTS.equals(type)) {
                intent = new Intent(SharedList.this, AdminSingleStudentView.class);
                intent.putExtra("selectedStudentName", selectedName);
                intent.putExtra("selectedStudentRfid", selectedId);
            } else if (TYPE_TEACHERS.equals(type)) {
                intent = new Intent(SharedList.this, AdminTeacherView.class);
                intent.putExtra("selectedTeacherName", selectedName);
                intent.putExtra("selectedTeacherId", selectedId);
            }
            else if (TYPE_ALUMNI.equals(type)) { //TODO: changing this
                intent = new Intent(SharedList.this, AlumniProfileView.class);
                intent.putExtra("selectedAlumniName", selectedName);
                intent.putExtra("selectedAlumniId", selectedId);
            }else {
                return;
            }
            intent.putExtra("campusID", campusID); // Pass campusID to the new activity
            startActivity(intent);
        });
    }
    private void filterItems(String searchText) {
        ArrayList<String> tempList = new ArrayList<>();
        String searchLower = searchText.toLowerCase().trim();
        for (String item : namesList) {
            if (item.toLowerCase().contains(searchLower)) {
                tempList.add(item);
            }
        }
        adapter.clear();
        adapter.addAll(tempList);
        adapter.notifyDataSetChanged();
    }
}
