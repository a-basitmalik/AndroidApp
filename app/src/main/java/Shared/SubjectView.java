package Shared;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.lms.R;


import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import Models.Subject;
import java.time.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SubjectView extends AppCompatActivity {
    private TextView subjectName;
    private TextView subjectId;
    private TextView subjectDay;
    private TextView subjectTime;
    private TextView teacherId;
    private TextView campusId;
    private TextView year;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_view);

        initializeViews();
        loadSubjectData();
    }

    private void initializeViews() {
        subjectName = findViewById(R.id.subject_name);
        subjectId = findViewById(R.id.subject_id);
        subjectDay = findViewById(R.id.subject_day);
        subjectTime = findViewById(R.id.subject_time);
        teacherId = findViewById(R.id.teacher_id);
        campusId = findViewById(R.id.campus_id);
        year = findViewById(R.id.year);
    }

    private void loadSubjectData() {
        // DB call todo

        Intent i = getIntent();
        String subjectName = i.getStringExtra("SubjectName");


        //TODO DB CALL
        Subject subject = new Subject.SubjectBuilder("Mathematics")
                .subjectId(1001)
                .day("Monday")
                .time(9, 0)  // 9:00 AM
                .teacherId(2001)
                .campusId(3001)
                .year(2024)
                .build();

        updateSubject(subject);
    }

    //update UI
    public void updateSubject(Subject subject) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        subjectName.setText(subject.getSubjectName());
        subjectId.setText("ID: " + subject.getSubjectId());
        subjectDay.setText(subject.getDay());
        subjectTime.setText(timeFormat.format(subject.getTime().getTime()));
        teacherId.setText(String.valueOf(subject.getTeacherId()));
        campusId.setText(String.valueOf(subject.getCampusId()));
        year.setText(String.valueOf(subject.getYear()));
    }
}