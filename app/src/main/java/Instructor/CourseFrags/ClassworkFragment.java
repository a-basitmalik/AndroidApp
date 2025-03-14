package Instructor.CourseFrags;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lms.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import Instructor.makeAssessment;
import Models.Assignment;

public class ClassworkFragment extends Fragment {
    private FloatingActionButton addSubjectFab;
    private CardView selectionPopup;
    private boolean isPopupVisible = false;

    private RecyclerView assignmentsRecyclerView;
    private RecyclerView monthlyRecyclerView;
    private RecyclerView sendupsRecyclerView;
    private RecyclerView mocksRecyclerView;

    private ClassworkAdapter assignmentsAdapter;
    private ClassworkAdapter monthlyAdapter;
    private ClassworkAdapter sendupsAdapter;
    private ClassworkAdapter mocksAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_classwork, container, false);

        initializeViews(view);
        setupRecyclerViews();
        setupListeners();
        loadData();

        return view;
    }


    private void initializeViews(View view) {
        addSubjectFab = view.findViewById(R.id.addSubjectFab);
        selectionPopup = view.findViewById(R.id.selectionPopup);

        assignmentsRecyclerView = view.findViewById(R.id.assignmentsRecyclerView);
        monthlyRecyclerView = view.findViewById(R.id.monthlyRecyclerView);
        sendupsRecyclerView = view.findViewById(R.id.sendupsRecyclerView);
        mocksRecyclerView = view.findViewById(R.id.mocksRecyclerView);

        // Setup individual option clicks
        view.findViewById(R.id.assignmentOption).setOnClickListener(v -> handleOptionClick("Assignment"));
        view.findViewById(R.id.monthlyOption).setOnClickListener(v -> handleOptionClick("Monthly"));
        view.findViewById(R.id.sendupsOption).setOnClickListener(v -> handleOptionClick("Send-ups"));
        view.findViewById(R.id.mocksOption).setOnClickListener(v -> handleOptionClick("Mocks"));
    }

    private void setupRecyclerViews() {
        // Setup adapters with dummy data
        assignmentsAdapter = new ClassworkAdapter(generateDummyAssignments());
        monthlyAdapter = new ClassworkAdapter(generateDummyMonthlyAssessments());
        sendupsAdapter = new ClassworkAdapter(generateDummySendups());
        mocksAdapter = new ClassworkAdapter(generateDummyMocks());

        // Setup RecyclerViews with respective adapters
        assignmentsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        assignmentsRecyclerView.setAdapter(assignmentsAdapter);

        monthlyRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        monthlyRecyclerView.setAdapter(monthlyAdapter);

        sendupsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        sendupsRecyclerView.setAdapter(sendupsAdapter);

        mocksRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mocksRecyclerView.setAdapter(mocksAdapter);
    }

    //TODO DB CALL
    private List<Assignment> generateDummyAssignments() {
        List<Assignment> assignments = new ArrayList<>();
        assignments.add(new Assignment(
                "Math Problem Set",
                "Solve chapters 5-7 problems",
                new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000L), // 7 days from now
                "Show all work and explain solutions"
        ));
        assignments.add(new Assignment(
                "Science Research Paper",
                "Write a detailed research paper on renewable energy",
                new Date(System.currentTimeMillis() + 14 * 24 * 60 * 60 * 1000L), // 14 days from now
                "Minimum 5 sources, APA format"
        ));
        return assignments;
    }

    private List<Assignment> generateDummyMonthlyAssessments() {
        List<Assignment> monthlyAssessments = new ArrayList<>();
        monthlyAssessments.add(new Assignment(
                "Monthly Physics Test",
                "Comprehensive test covering electromagnetic theory",
                new Date(System.currentTimeMillis() + 30 * 24 * 60 * 60 * 1000L), // 30 days from now
                "Bring scientific calculator"
        ));
        return monthlyAssessments;
    }

    private List<Assignment> generateDummySendups() {
        List<Assignment> sendups = new ArrayList<>();
        sendups.add(new Assignment(
                "Chemistry Send-up Exam",
                "Comprehensive review of first semester content",
                new Date(System.currentTimeMillis() + 45 * 24 * 60 * 60 * 1000L), // 45 days from now
                "Cumulative exam, all chapters"
        ));
        return sendups;
    }

    private List<Assignment> generateDummyMocks() {
        List<Assignment> mockExams = new ArrayList<>();
        mockExams.add(new Assignment(
                "Biology Mock Exam",
                "Simulated board exam practice",
                new Date(System.currentTimeMillis() + 60 * 24 * 60 * 60 * 1000L), // 60 days from now
                "Timed exam, board exam format"
        ));
        return mockExams;
    }


    private void setupListeners() {
        addSubjectFab.setOnClickListener(v -> togglePopupVisibility());

        // Close popup when clicking outside
        View rootView = getView();
        if (rootView != null) {
            rootView.setOnTouchListener((v, event) -> {
                if (isPopupVisible) {
                    hidePopup();
                    return true;
                }
                return false;
            });
        }
    }

    private void loadData() {
        //TODO DB CALL
    }

    private void togglePopupVisibility() {
        if (isPopupVisible) {
            hidePopup();
        } else {
            showPopup();
        }
    }

    private void showPopup() {
        selectionPopup.setVisibility(View.VISIBLE);
        isPopupVisible = true;
    }

    private void hidePopup() {
        selectionPopup.setVisibility(View.GONE);
        isPopupVisible = false;
    }

    private void handleOptionClick(String option) {

        Toast.makeText(getContext(), "Creating new " + option, Toast.LENGTH_SHORT).show();
        hidePopup();


        Intent intent;
        intent = new Intent(getContext(), makeAssessment.class);

        switch (option) {
            case "Assignment":
                intent.putExtra("type", "Assignment");

                break;
            case "Monthly":
                intent.putExtra("type", "Monthly");

                break;
            case "Send-ups":
                intent.putExtra("type", "Send-ups");

                break;
            case "Mocks":
                intent.putExtra("type", "Mocks");

                break;
            default:
                return;
        }
        startActivity(intent);
    }


}
 class ClassworkAdapter extends RecyclerView.Adapter<ClassworkAdapter.ViewHolder> {
    private List<Assignment> assignments;
    private SimpleDateFormat dateFormat;

    public ClassworkAdapter(List<Assignment> assignments) {
        this.assignments = assignments;
        this.dateFormat = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.assig_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Assignment assignment = assignments.get(position);
        holder.titleTextView.setText(assignment.getTitle());
        holder.descriptionTextView.setText(assignment.getDescription());
        holder.dueDateTextView.setText(dateFormat.format(assignment.getDueDate()));
    }

    @Override
    public int getItemCount() {
        return assignments.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView descriptionTextView;
        TextView dueDateTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.assignmentTitleTextView);
            descriptionTextView = itemView.findViewById(R.id.assignmentDescriptionTextView);
            dueDateTextView = itemView.findViewById(R.id.dueDateTextView);
        }
    }
}