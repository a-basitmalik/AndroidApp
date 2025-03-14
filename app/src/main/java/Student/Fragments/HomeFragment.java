package Student.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.lms.R;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import Student.ViewAllAnnounc;

public class HomeFragment extends Fragment {

    private LinearLayout scheduleContainer;
    private LinearLayout recentAnnouncementsContainer;
    private List<ScheduleItem> todaySchedule;
    private List<Announcement> recentAnnouncements;

    private TextView announcement1Title;
    private TextView announcement1Date;
    private TextView announcement2Title;
    private TextView announcement2Date;

    private static class ScheduleItem {
        String time;
        String subject;

        ScheduleItem(String time, String subject) {
            this.time = time;
            this.subject = subject;
        }
    }

    private static class Announcement {
        String title;
        String date;

        Announcement(String title, String date) {
            this.title = title;
            this.date = date;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_stu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        scheduleContainer = view.findViewById(R.id.scheduleContainer);
        announcement1Title = view.findViewById(R.id.announcement_1_title);
        announcement1Date = view.findViewById(R.id.announcement_1_date);
        announcement2Title = view.findViewById(R.id.announcement_2_title);
        announcement2Date = view.findViewById(R.id.announcement_2_date);

        setupDummyScheduleData();
        setupDummyAnnouncementsData();
        setupScheduleView();
        setupAnnouncementsView();
        setupAnnouncementsButton(view);
    }

    private void setupDummyScheduleData() {
        todaySchedule = new ArrayList<>();
        todaySchedule.add(new ScheduleItem("09:00 AM", "Mathematics"));
        todaySchedule.add(new ScheduleItem("12:00 PM", "Break"));
        todaySchedule.add(new ScheduleItem("01:00 PM", "Physics"));
        todaySchedule.add(new ScheduleItem("03:00 PM", "Arts"));
    }

    private void setupDummyAnnouncementsData() {
        recentAnnouncements = new ArrayList<>();
        recentAnnouncements.add(new Announcement("Midterm Exam Schedule Released", "Jan 22, 2024"));
        recentAnnouncements.add(new Announcement("New Assignment Uploaded for Physics", "Jan 20, 2024"));
    }

    private void setupScheduleView() {
        scheduleContainer.removeAllViews();
        for (ScheduleItem item : todaySchedule) {
            View scheduleItemView = getLayoutInflater().inflate(R.layout.schedule_item, scheduleContainer, false);
            TextView timeTextView = scheduleItemView.findViewById(R.id.scheduleTime);
            TextView subjectTextView = scheduleItemView.findViewById(R.id.scheduleSubject);

            timeTextView.setText(item.time);
            subjectTextView.setText(item.subject);

            scheduleContainer.addView(scheduleItemView);
        }
    }

    private void setupAnnouncementsView() {

        if (recentAnnouncements.size() > 0) {
            announcement1Title.setText(recentAnnouncements.get(0).title);
            announcement1Date.setText(recentAnnouncements.get(0).date);
        }

        if (recentAnnouncements.size() > 1) {
            announcement2Title.setText(recentAnnouncements.get(1).title);
            announcement2Date.setText(recentAnnouncements.get(1).date);
        }
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
}