package Shared.Attendance;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.lms.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class EditAttendance extends AppCompatActivity {

    private static final String TAG = "EditAttendance";


    private ImageButton backButton;
    private TextInputEditText datePickerEdit;
    private TextInputLayout datePickerLayout;
    private AutoCompleteTextView classSpinner;
    private TextInputLayout classSpinnerLayout;
    private RecyclerView studentsRecyclerView;
    private MaterialButton cancelButton;
    private MaterialButton saveButton;
    private TextView infoTextView;


    private int campusId;
    private String userType;
    private String selectedDate;
    private String selectedClass = "";
    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private List<AttendanceRecord> attendanceRecords;
    private AttendanceAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_attendance);

        // Get intent data
        campusId = getIntent().getIntExtra("campusID", -1);
        userType = getIntent().getStringExtra("userType");
        if (userType == null) {
            userType = "Student"; // Default to Student if not provided
        }

        // Initialize date format and calendar
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        calendar = Calendar.getInstance();

        initializeViews();
        setupBackButton();
        setupDatePicker();
        setupClassSpinner();
        setupRecyclerView();
        setupButtons();
    }

    private void initializeViews() {
        backButton = findViewById(R.id.backButton);
        datePickerEdit = findViewById(R.id.datePickerEdit);
        datePickerLayout = findViewById(R.id.datePickerLayout);
        classSpinner = findViewById(R.id.classSpinner);
        classSpinnerLayout = findViewById(R.id.classSpinnerLayout);
        studentsRecyclerView = findViewById(R.id.studentsRecyclerView);
        cancelButton = findViewById(R.id.cancelButton);
        saveButton = findViewById(R.id.saveButton);
        infoTextView = findViewById(R.id.infoTextView);
    }

    private void setupBackButton() {

        backButton.setOnClickListener(v -> onBackPressed());
    }

    private void setupDatePicker() {

        selectedDate = dateFormat.format(calendar.getTime());
        datePickerEdit.setText(selectedDate);


        datePickerEdit.setOnClickListener(v -> showDatePicker());
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    selectedDate = dateFormat.format(calendar.getTime());
                    datePickerEdit.setText(selectedDate);
                    fetchAttendanceRecords();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );


        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        datePickerDialog.show();
    }

    private void setupClassSpinner() {
        String[] classes = {"Class 1", "Class 2", "Class 3", "Class 4", "Class 5",
                "Class 6", "Class 7", "Class 8", "Class 9", "Class 10"};

        ArrayAdapter<String> classAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, classes);
        classSpinner.setAdapter(classAdapter);

        // Set listener
        classSpinner.setOnItemClickListener((parent, view, position, id) -> {
            selectedClass = classes[position];
            fetchAttendanceRecords();
        });
    }

    private void setupRecyclerView() {
        attendanceRecords = new ArrayList<>();
        adapter = new AttendanceAdapter(this, attendanceRecords);
        studentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        studentsRecyclerView.setAdapter(adapter);
    }

    private void setupButtons() {
        cancelButton.setOnClickListener(v -> onBackPressed());
        saveButton.setOnClickListener(v -> saveAttendanceChanges());
    }

    private void fetchAttendanceRecords() {
        if (selectedDate.isEmpty()) {
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show();
            return;
        }

        return;
    }

    private void parseAttendanceRecords(JSONArray recordsArray) throws JSONException {
        attendanceRecords.clear();

        for (int i = 0; i < recordsArray.length(); i++) {
            JSONObject record = recordsArray.getJSONObject(i);

            int id = record.getInt("id");
            String name = record.getString("name");
            String rollNumber = record.getString("roll_number");
            String profilePic = record.optString("profile_pic", "");
            String status = record.getString("status");

            AttendanceRecord attendanceRecord = new AttendanceRecord(id, name, rollNumber, profilePic, status);
            attendanceRecords.add(attendanceRecord);
        }

        adapter.notifyDataSetChanged();
    }

    private void saveAttendanceChanges() {

    }


    public static class AttendanceRecord {
        private int id;
        private String name;
        private String rollNumber;
        private String profilePic;
        private String status;
        private String originalStatus;
        private boolean statusChanged;

        public AttendanceRecord(int id, String name, String rollNumber, String profilePic, String status) {
            this.id = id;
            this.name = name;
            this.rollNumber = rollNumber;
            this.profilePic = profilePic;
            this.status = status;
            this.originalStatus = status;
            this.statusChanged = false;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getRollNumber() {
            return rollNumber;
        }

        public String getProfilePic() {
            return profilePic;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
            this.statusChanged = !status.equals(originalStatus);
        }

        public boolean isStatusChanged() {
            return statusChanged;
        }

        public void resetStatusChanged() {
            this.originalStatus = status;
            this.statusChanged = false;
        }
    }


    class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.AttendanceViewHolder> {
        private Context context;
        private List<AttendanceRecord> attendanceRecords;

        public AttendanceAdapter(Context context, List<AttendanceRecord> attendanceRecords) {
            this.context = context;
            this.attendanceRecords = attendanceRecords;
        }

        @Override
        public AttendanceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_edit_attendance, parent, false);
            return new AttendanceViewHolder(view);
        }

        @Override
        public void onBindViewHolder(AttendanceViewHolder holder, int position) {
            AttendanceRecord record = attendanceRecords.get(position);


            holder.tvName.setText(record.getName());
            holder.tvRollNumber.setText("Roll #: " + record.getRollNumber());
            holder.tvCurrentStatus.setText("Currently: " + record.getStatus());


            int statusColor = record.getStatus().equals("Present") ? R.color.success_green :
                    record.getStatus().equals("Absent") ? R.color.error_color :
                            R.color.warning_color;
            holder.tvCurrentStatus.setTextColor(ContextCompat.getColor(context, statusColor));
        }

        @Override
        public int getItemCount() {
            return attendanceRecords.size();
        }

        class AttendanceViewHolder extends RecyclerView.ViewHolder {
            ShapeableImageView ivProfilePic;
            TextView tvName, tvRollNumber, tvCurrentStatus;
            RadioGroup rgAttendanceStatus;

            public AttendanceViewHolder(View itemView) {
                super(itemView);
                //ivProfilePic = itemView.findViewById(R.id.ivProfilePic);
                tvName = itemView.findViewById(R.id.tvName);


            }
        }
    }
}