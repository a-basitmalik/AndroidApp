package Instructor;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.lms.R;


public class makeAssessment extends AppCompatActivity {

    private Spinner spinnerAssessmentType, spinnerSubject;
    private EditText etTotalMarks, etGradeAPlus, etGradeA, etGradeB, etGradeC, etGradeD;
    private Button btnCreateAssessment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_assessment);

        Intent i = getIntent();
        String type = i.getStringExtra("type");
         TextView header = findViewById(R.id.header);
        header.setText("Make " + type);



//        spinnerAssessmentType = findViewById(R.id.spinnerAssessmentType);
//        spinnerSubject = findViewById(R.id.spinnerSubject);
        etTotalMarks = findViewById(R.id.etTotalMarks);
        etGradeAPlus = findViewById(R.id.etGradeAPlus);
        etGradeA = findViewById(R.id.etGradeA);
        etGradeB = findViewById(R.id.etGradeB);
        etGradeC = findViewById(R.id.etGradeC);
        etGradeD = findViewById(R.id.etGradeD);
        btnCreateAssessment = findViewById(R.id.btnCreateAssessment);


        btnCreateAssessment.setOnClickListener(v -> {

          //  String assessmentType = spinnerAssessmentType.getSelectedItem().toString();
           // String subject = spinnerSubject.getSelectedItem().toString();
            String totalMarks = etTotalMarks.getText().toString();
            String gradeAPlus = etGradeAPlus.getText().toString();

            if (totalMarks.isEmpty() || gradeAPlus.isEmpty()) {
                Toast.makeText(makeAssessment.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                //  TODO DB CALL -> upload
                Toast.makeText(makeAssessment.this, "Assessment Created!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}