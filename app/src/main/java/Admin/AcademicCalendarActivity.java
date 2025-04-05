package Admin;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lms.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;

import com.google.android.material.appbar.MaterialToolbar;

public class AcademicCalendarActivity extends AppCompatActivity {

    private RecyclerView eventsRecyclerView;
    private MaterialButtonToggleGroup yearToggleGroup, monthToggleGroup;
    private MaterialButton previousYearBtn, currentYearBtn, nextYearBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_academic_calendar);

        // Set up the toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Academic Calendar");

        // Set up the RecyclerView for academic events
        eventsRecyclerView = findViewById(R.id.eventsRecyclerView);
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Assuming a custom adapter has been set up for the RecyclerView
        //eventsRecyclerView.setAdapter(new EventsAdapter());

        // Set up the Year Selection buttons
        yearToggleGroup = findViewById(R.id.yearToggleGroup);
        previousYearBtn = findViewById(R.id.previousYearBtn);
        currentYearBtn = findViewById(R.id.currentYearBtn);
        nextYearBtn = findViewById(R.id.nextYearBtn);

        // Set up the Month Selection buttons
        monthToggleGroup = findViewById(R.id.monthToggleGroup);
        // Set up the buttons for months (January to December)
        MaterialButton janBtn = findViewById(R.id.janBtn);
        MaterialButton febBtn = findViewById(R.id.febBtn);
        MaterialButton marBtn = findViewById(R.id.marBtn);
        MaterialButton aprBtn = findViewById(R.id.aprBtn);
        MaterialButton mayBtn = findViewById(R.id.mayBtn);
        MaterialButton junBtn = findViewById(R.id.junBtn);
        MaterialButton julBtn = findViewById(R.id.julBtn);
        MaterialButton augBtn = findViewById(R.id.augBtn);
        MaterialButton sepBtn = findViewById(R.id.sepBtn);
        MaterialButton octBtn = findViewById(R.id.octBtn);
        MaterialButton novBtn = findViewById(R.id.novBtn);
        MaterialButton decBtn = findViewById(R.id.decBtn);

        // Example of adding functionality to month selection (using a listener)
        janBtn.setOnClickListener(v -> updateCalendarForMonth("January"));
        febBtn.setOnClickListener(v -> updateCalendarForMonth("February"));
        marBtn.setOnClickListener(v -> updateCalendarForMonth("March"));
        // Repeat for all months...

        // Example of setting year selection
        yearToggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (checkedId == R.id.previousYearBtn) {
                updateCalendarForYear("2022-23");
            } else if (checkedId == R.id.currentYearBtn) {
                updateCalendarForYear("2023-24");
            } else if (checkedId == R.id.nextYearBtn) {
                updateCalendarForYear("2024-25");
            }
        });
    }

    private void updateCalendarForMonth(String month) {
        // Logic for updating the calendar based on selected month
        // This could involve filtering events or updating a UI component
    }

    private void updateCalendarForYear(String year) {
        // Logic for updating the calendar based on selected year
        // This could involve filtering events or updating a UI component
    }
}