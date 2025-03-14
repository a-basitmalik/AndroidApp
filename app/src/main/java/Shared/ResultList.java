package Shared;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RetryPolicy;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.lms.R;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.List;
import android.content.Intent;
import android.widget.Filter;
import android.widget.Filterable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import Models.Student;

public class ResultList extends AppCompatActivity {
    private ListView studentListView;
    private List<Student> studentList;
    private StudentAdapter adapter;
    private TextInputEditText searchEditText;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_list);

        studentListView = findViewById(R.id.studentListView);
        searchEditText = findViewById(R.id.searchEditText);

        studentList = new ArrayList<>();
        adapter = new StudentAdapter(this, studentList);
        studentListView.setAdapter(adapter);

        requestQueue = Volley.newRequestQueue(this);

        fetchStudentsData(); // Fetch student data from API

        studentListView.setOnItemClickListener((parent, view, position, id) -> {
            Student student = adapter.getItem(position);  // Ensure filtered item is retrieved
            if (student != null) {
                openStudentDetails(student);
            }
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void fetchStudentsData() {
        Intent intent = getIntent();
        int campusId = intent.getIntExtra("campusID", 1);

        // Ensure campusId is included in the API request
        String url = "http://193.203.162.232:5050/result/get_students?campus_id=" + campusId;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray studentsArray = response.getJSONArray("students");
                        studentList.clear();

                        for (int i = 0; i < studentsArray.length(); i++) {
                            JSONObject studentObj = studentsArray.getJSONObject(i);
                            String id = studentObj.getString("student_id");
                            String name = studentObj.getString("name");
                            String phone = studentObj.getString("phone");
                            int year = studentObj.getInt("year");

                            studentList.add(new Student(id, name, phone, year));
                        }

                        adapter.updateList(studentList); // Update adapter with new data
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> error.printStackTrace()) {

            @Override
            public RetryPolicy getRetryPolicy() {
                return new DefaultRetryPolicy(
                        20000, // 20 seconds timeout
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES, // Default retry count
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT // Default backoff multiplier
                );
            }
        };

        requestQueue.add(jsonObjectRequest);
    }


    private void openStudentDetails(Student student) {
        Intent intent = new Intent(this, AssessmentTypeActivity.class);

        intent.putExtra("STUDENT_ID", student.getStudentId());

        intent.putExtra("STUDENT_NAME", student.getStudentName());
        intent.putExtra("STUDENT_PHONE", student.getPhoneNumber());
        intent.putExtra("STUDENT_YEAR", student.getYear());
        startActivity(intent);
    }
}

class StudentAdapter extends ArrayAdapter<Student> implements Filterable {
    private List<Student> originalList;
    private List<Student> filteredList;
    private StudentFilter filter;
    private Context context;

    public StudentAdapter(Context context, List<Student> students) {
        super(context, 0, students);
        this.context = context;
        this.originalList = new ArrayList<>(students);
        this.filteredList = new ArrayList<>(students);
    }

    public void updateList(List<Student> newList) {
        this.originalList.clear();
        this.originalList.addAll(newList);
        this.filteredList.clear();
        this.filteredList.addAll(newList);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return filteredList.size();
    }

    @Override
    public Student getItem(int position) {
        return filteredList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.student_res_list_item, parent, false);
        }

        Student student = getItem(position);

        if (student != null) {
            TextView nameText = convertView.findViewById(R.id.studentName);
            TextView idText = convertView.findViewById(R.id.studentId);
            TextView yearText = convertView.findViewById(R.id.year);

            nameText.setText(student.getStudentName());
            idText.setText(String.valueOf(student.getStudentId()));
            yearText.setText("Year " + student.getYear());
        }

        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new StudentFilter();
        }
        return filter;
    }

    private class StudentFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<Student> filteredItems = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredItems.addAll(originalList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Student student : originalList) {
                    if (student.getStudentName().toLowerCase().contains(filterPattern) ||
                            String.valueOf(student.getStudentId()).contains(filterPattern)) {
                        filteredItems.add(student);
                    }
                }
            }

            results.values = filteredItems;
            results.count = filteredItems.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredList.clear();
            filteredList.addAll((List<Student>) results.values);
            notifyDataSetChanged();
        }
    }
}
