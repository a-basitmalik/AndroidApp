package Instructor.Fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.lms.R;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import Student.ViewAllAnnounc;

public class HomeFragment extends Fragment {
    private LinearLayout scheduleContainer;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_inst, container, false);

        scheduleContainer = view.findViewById(R.id.scheduleContainer);
        setupSchedule();
        setupAnnouncementsButton(view);

        return view;
    }

    private void setupSchedule() {
        List<ScheduleItem> scheduleItems = getDummyScheduleData();

        for (ScheduleItem item : scheduleItems) {
            View scheduleItemView = getLayoutInflater().inflate(R.layout.schedule_item, null);

            TextView timeTextView = scheduleItemView.findViewById(R.id.scheduleTime);
            TextView titleTextView = scheduleItemView.findViewById(R.id.scheduleSubject);

            timeTextView.setText(item.getTime());
            titleTextView.setText(item.getTitle());

            scheduleContainer.addView(scheduleItemView);
        }
    }

    private List<ScheduleItem> getDummyScheduleData() {
        List<ScheduleItem> scheduleItems = new ArrayList<>();
        scheduleItems.add(new ScheduleItem("08:00 AM", "Mathematics Class (Grade 10)"));
        scheduleItems.add(new ScheduleItem("03:30 PM", "Parent-Teacher Meeting"));
        return scheduleItems;
    }

    private void setupAnnouncementsButton(View view) {
        MaterialButton viewAllButton = view.findViewById(R.id.btn_view_all_announcements);
        if (viewAllButton != null) {
            viewAllButton.setOnClickListener(v -> {
                Intent intent = new Intent(requireContext(), ViewAllAnnounc.class);
                startActivity(intent);
            });
        }
    }

    private static class ScheduleItem {
        private final String time;
        private final String title;

        public ScheduleItem(String time, String title) {
            this.time = time;
            this.title = title;
        }

        public String getTime() { return time; }
        public String getTitle() { return title; }
    }
}