package Admin;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lms.R;

import android.content.Intent;
import android.net.Uri;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import Models.Alumni;

public class AlumniProfileView extends AppCompatActivity {

    private Alumni alumniProfile;
    private TextView alumniNameTitle, alumniEmail, alumniPhone;
    private TextView alumniUniversity, alumniDegree, alumniGraduationYear;
    private TextView alumniGPA, alumniStanding, alumniThesis;
    private ImageView alumniPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alumni_profile_view);

        //TODO db call
        loadAlumniData();

        setupToolbar();
        initializeViews();
        populateViews();
        setupClickListeners();
    }

    private void loadAlumniData() {

        alumniProfile = new Alumni(
                "John Doe",
                "johndoe@example.com",
                "+1 (555) 123-4567",
                "https://example.com/alumni/photo/johndoe.jpg", // URL to alumni photo
                "Massachusetts Institute of Technology",
                "Bachelor of Science in Computer Science",
                "2022",
                "3.85 / 4.0",
                "Summa Cum Laude",
                "Machine Learning Applications in Educational Technology"
        );
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsingToolbar);
        collapsingToolbarLayout.setTitle("Alumni Profile");
    }

    private void initializeViews() {

        alumniNameTitle = findViewById(R.id.alumniNameTitle);
        alumniEmail = findViewById(R.id.alumniEmail);
        alumniPhone = findViewById(R.id.alumniPhone);
        alumniPhoto = findViewById(R.id.alumniPhoto);


        alumniUniversity = findViewById(R.id.alumniUniversity);
        alumniDegree = findViewById(R.id.alumniDegree);
        alumniGraduationYear = findViewById(R.id.alumniGraduationYear);


        alumniGPA = findViewById(R.id.alumniGPA);
        alumniStanding = findViewById(R.id.alumniStanding);
        alumniThesis = findViewById(R.id.alumniThesis);


        FloatingActionButton editProfileFab = findViewById(R.id.editProfileFab);
        editProfileFab.setOnClickListener(v -> {
            Toast.makeText(this, "Edit profile functionality", Toast.LENGTH_SHORT).show();
        });
    }

    private void populateViews() {

        alumniNameTitle.setText(alumniProfile.getName());
        alumniEmail.setText(alumniProfile.getEmail());
        alumniPhone.setText(alumniProfile.getPhone());

        //TODO load picture

        alumniUniversity.setText(alumniProfile.getUniversity());
        alumniDegree.setText(alumniProfile.getDegree());
        alumniGraduationYear.setText("Class of " + alumniProfile.getGraduationYear());


        alumniGPA.setText(alumniProfile.getGpa());
        alumniStanding.setText(alumniProfile.getStanding());
        alumniThesis.setText(alumniProfile.getThesis());
    }

    private void setupClickListeners() {
        alumniEmail.setOnClickListener(v -> {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:" + alumniProfile.getEmail()));

            if (emailIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(emailIntent);
            } else {
                Toast.makeText(this, "No email app found", Toast.LENGTH_SHORT).show();
            }
        });


        alumniPhone.setOnClickListener(v -> {
            Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
            phoneIntent.setData(Uri.parse("tel:" + alumniProfile.getPhone()));

            if (phoneIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(phoneIntent);
            } else {
                Toast.makeText(this, "No phone app found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}