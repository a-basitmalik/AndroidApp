package Instructor.CourseFrags;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.lms.R;

import java.util.ArrayList;
import java.util.List;

import Instructor.QueryList;
import Models.Query;


public class GeneralFragment extends Fragment {
    private LinearLayout scheduleContainer;
    private LinearLayout queriesContainer;
    private List<ScheduleItem> todaySchedule;
    private List<Query> recentQueries;
    private TextView subjectName;

    private static class ScheduleItem {
        String time;
        String subject;

        ScheduleItem(String time, String subject) {
            this.time = time;
            this.subject = subject;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_general, container, false);
    }

    String getSubjectName(){
        return "Math";
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        scheduleContainer = view.findViewById(R.id.scheduleContainer);
        queriesContainer = view.findViewById(R.id.queriesContainer);
        subjectName = view.findViewById(R.id.schedule_header);
        subjectName.setText(getSubjectName() + " Schedule");


        setupDummyData();
        populateSchedule();
        populateQueries();
        setupViewAllQueries(view);
    }


    //DB CALL TODO
    private void setupDummyData() {
        todaySchedule = new ArrayList<>();
        //IMPROVE TODO
        todaySchedule.add(new ScheduleItem("09:00 AM", "Mathematics"));
        todaySchedule.add(new ScheduleItem("12:00 PM", "Break"));
        todaySchedule.add(new ScheduleItem("01:00 PM", "Mathematics"));




        recentQueries = new ArrayList<>();
        recentQueries.add(new Query(
                "Math Teacher",
                "When is the next assignment due?",
                "The next assignment is due on Monday, January 20th.",
                "Responded", // Status as a String
                "Jan 15, 2025"
        ));

        recentQueries.add(new Query(
                "Math Teacher",
                "Can we discuss the lab report tomorrow?",
                null, // No response yet
                "Pending", // Status as a String
                "Jan 16, 2025"
        ));
        // Set response for one query to show different states
        recentQueries.get(0).setResponse("The assignment is due next Friday.");
    }

    private void populateSchedule() {
        LayoutInflater inflater = LayoutInflater.from(requireContext());

        for (ScheduleItem item : todaySchedule) {
            View scheduleItemView = inflater.inflate(R.layout.schedule_item, scheduleContainer, false);

            TextView timeView = scheduleItemView.findViewById(R.id.scheduleTime);
            TextView subjectView = scheduleItemView.findViewById(R.id.scheduleSubject);

            timeView.setText(item.time);
            subjectView.setText(item.subject);

            scheduleContainer.addView(scheduleItemView);
        }
    }

    private void populateQueries() {
        LayoutInflater inflater = LayoutInflater.from(requireContext());

        // Show only up to 2 queries
        for (int i = 0; i < Math.min(2, recentQueries.size()); i++) {
            Query query = recentQueries.get(i);
            View queryItemView = inflater.inflate(R.layout.query_item, queriesContainer, false);

            TextView recipientView = queryItemView.findViewById(R.id.recipientName);
            TextView queryTextView = queryItemView.findViewById(R.id.queryText);
            TextView responseTextView = queryItemView.findViewById(R.id.responseText);
            com.google.android.material.chip.Chip statusChip = queryItemView.findViewById(R.id.statusChip);
            TextView submissionDateView = queryItemView.findViewById(R.id.submissionDate);

            // Set recipient name
            recipientView.setText(query.getRecipient());

            // Set query text
            queryTextView.setText(query.getQueryText());

            // Set response text and visibility
            if (query.getResponse() != null && !query.getResponse().isEmpty()) {
                responseTextView.setText(query.getResponse());
                responseTextView.setVisibility(View.VISIBLE);
            } else {
                responseTextView.setVisibility(View.GONE);
            }

            // Set status chip text and color
            statusChip.setText(query.getStatus());
            //TODO set color

            // Set submission date
            submissionDateView.setText(String.format("Submitted on: %s", query.getSubmissionDate()));

            // Add the populated view to the container
            queriesContainer.addView(queryItemView);
        }
    }

    private void setupViewAllQueries(View view) {
        TextView viewAllQueries = view.findViewById(R.id.viewAllQueries);
        viewAllQueries.setOnClickListener(v -> {
            Intent i = new Intent(getContext(), QueryList.class);
            startActivity(i);
            //TODO pass teacher name?
        });
    }
}