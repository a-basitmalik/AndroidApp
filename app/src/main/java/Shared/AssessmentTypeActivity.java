package Shared;
import com.example.lms.R;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONObject;

public class AssessmentTypeActivity extends AppCompatActivity {

    private ListView assessmentTypeListView;
    private ArrayAdapter<String> adapter;
    private List<String> assessmentTypeList;
    private RequestQueue requestQueue;
    private String studentId = "12345";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_types);

        assessmentTypeListView = findViewById(R.id.assessmentTypeListView);
        assessmentTypeList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, assessmentTypeList);
        assessmentTypeListView.setAdapter(adapter);

        requestQueue = Volley.newRequestQueue(this);
        fetchAssessmentTypes();

        assessmentTypeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedType = assessmentTypeList.get(position);

                // Retrieve student ID if passed from the previous activity
                Intent intent = getIntent();
                if (intent != null) {
                    studentId = intent.getStringExtra("STUDENT_ID");
                }

                // Start SingleResult activity with selected type
                intent = new Intent(AssessmentTypeActivity.this, SingleResult.class);
                Log.d("AssessmentTypeActivity", "Passing data: Student ID = " + studentId + ", Type = " + selectedType);
                intent.putExtra("STUDENT_ID", studentId);
                intent.putExtra("type", selectedType);
                startActivity(intent);
            }
        });
    }

    private void fetchAssessmentTypes() {
        String url = "http://193.203.162.232:5050/result/get_assessment_types";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray typesArray = response.getJSONArray("assessment_types");
                            Set<String> uniqueTypes = new HashSet<>();
                            for (int i = 0; i < typesArray.length(); i++) {
                                uniqueTypes.add(typesArray.getString(i));
                            }
                            assessmentTypeList.clear();
                            assessmentTypeList.addAll(uniqueTypes);
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });

        jsonObjectRequest.setRetryPolicy(new com.android.volley.DefaultRetryPolicy(
                20000,
                com.android.volley.DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                com.android.volley.DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        requestQueue.add(jsonObjectRequest);
    }
}
