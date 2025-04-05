package Admin;
import android.util.Log;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.example.lms.MainActivity;
import com.example.lms.R;

import Admin.Subjects.SubjectDashboard;
import Shared.Attendance.AttendanceDashboard;
import Admin.Fee.FeeDashboard;
import Shared.ResultList;
import Shared.SharedList;

public class AdminDashboard extends AppCompatActivity implements View.OnClickListener {

    private String campusName = "School Management";
    private int campusID=1;
    private Toolbar toolbar;
    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Admin Dashboard");
        }

        toolbar.setNavigationIcon(R.drawable.ic_menu);
        toolbar.setNavigationOnClickListener(v -> {

        });
    }
//    @Override
//    public boolean onNavigationItemSelected(MenuItem item) {
//        int id = item.getItemId();

//        if (id == R.id.nav_classes) {
//            // Handle Classes action
//        } else if (id == R.id.nav_calendar) {
//            // Handle Calendar action
//        }
//
//        DrawerLayout drawer = findViewById(R.id.admi);
//        drawer.closeDrawer(GravityCompat.START);
//        return true;
    //}

    void initManagementCards(){


        CardView studentManagementCard = findViewById(R.id.student_management_card);
        CardView teacherManagementCard = findViewById(R.id.teacher_management_card);
        CardView subjectsCurriculumCard = findViewById(R.id.subject_management_card);
        CardView attendanceCard = findViewById(R.id.attendance_management_card);
        CardView resultsCard = findViewById(R.id.result_management_card);
        CardView feeManagementCard = findViewById(R.id.fee_management_card);
        CardView alumniCard = findViewById(R.id.alumni_management_card);
        CardView announcementCard = findViewById(R.id.ann_management_card);
        CardView academicCal = findViewById(R.id.academic_cal);


        studentManagementCard.setOnClickListener(this);
        teacherManagementCard.setOnClickListener(this);
        subjectsCurriculumCard.setOnClickListener(this);
        attendanceCard.setOnClickListener(this);
        resultsCard.setOnClickListener(this);
        feeManagementCard.setOnClickListener(this);
        alumniCard.setOnClickListener(this);
        announcementCard.setOnClickListener(this);
        academicCal.setOnClickListener(this);


        campusName = getIntent().getStringExtra("campusName");
        campusID=getIntent().getIntExtra("campusID", 1);
        Log.d("SharedList", "Received campusID: " + campusID);
        TextView header = findViewById(R.id.header);
        header.setText(campusName);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);
     //   initToolbar();
        initManagementCards();;

    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();

        if (viewId == R.id.student_management_card) {
            navigateToActivity(SharedList.class, "students");
        } else if (viewId == R.id.teacher_management_card) {
            navigateToActivity(SharedList.class, "teachers");
        } else if (viewId == R.id.subject_management_card) {
            navigateToActivity(SubjectDashboard.class, "Subjects and Curriculum");
        } else if (viewId == R.id.attendance_management_card) {
            navigateToActivity(AttendanceDashboard.class, "Attendance Management");
        } else if (viewId == R.id.result_management_card) {
            navigateToActivity(ResultList.class, "Result Management");
        } else if (viewId == R.id.fee_management_card) {
            navigateToActivity(FeeDashboard.class, "Attendance Management");

        } else if (viewId == R.id.alumni_management_card) {
            navigateToActivity(SharedList.class, "alumni");
        }
        else if (viewId == R.id.ann_management_card){
            navigateToActivity(Announcement.class, "Announcement");
        }
        else if (viewId == R.id.academic_cal){
            navigateToActivity(AcademicCalendarActivity.class, "Academic Calander");
        }
    }

    private void navigateToActivity(Class<?> targetActivity, String managementType) {

        Intent intent = new Intent(this, targetActivity);
        intent.putExtra("type", managementType);
        intent.putExtra("Campus Name", campusName);
        intent.putExtra("campusID", campusID);
        startActivity(intent);
    }


    private void handleUnimplementedFeature(String featureName) {


    }



}