package Student;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.lms.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import Models.Announcement;

public class ViewAllAnnounc extends AppCompatActivity {
    private RecyclerView recyclerView;
    private AnnouncementAdapter adapter;
    private SwipeRefreshLayout swipeRefresh;
    private TextView emptyView;
    private TabLayout tabLayout;
    private List<Announcement> allAnnouncements;
    private List<Announcement> unreadAnnouncements;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_announc);


        initializeViews();
        setupToolbar();
        setupRecyclerView();
        setupSwipeRefresh();
        setupTabLayout();


        loadAnnouncements();
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.announcements_recycler_view);
        swipeRefresh = findViewById(R.id.swipe_refresh);
        emptyView = findViewById(R.id.empty_view);
        tabLayout = findViewById(R.id.tab_layout);


        allAnnouncements = new ArrayList<>();
        unreadAnnouncements = new ArrayList<>();


        adapter = new AnnouncementAdapter(this, allAnnouncements);
        recyclerView.setAdapter(adapter);
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new AnnouncementItemDecoration());
    }

    private void setupSwipeRefresh() {
        swipeRefresh.setColorSchemeResources(R.color.primary_color_light);
        swipeRefresh.setOnRefreshListener(this::loadAnnouncements);
    }

    private void setupTabLayout() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                updateAnnouncementsList(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void loadAnnouncements() {
        swipeRefresh.setRefreshing(true);
        executorService.execute(() -> {

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //TODO DB CALL
            List<Announcement> loadedAnnouncements = getDummyAnnouncements();

            mainHandler.post(() -> {
                allAnnouncements = loadedAnnouncements;
                unreadAnnouncements = allAnnouncements.stream()
                        .filter(announcement -> !announcement.isRead())
                        .collect(Collectors.toList());

                updateAnnouncementsList(tabLayout.getSelectedTabPosition());
                swipeRefresh.setRefreshing(false);
            });
        });
    }

    private List<Announcement> getDummyAnnouncements() {
        List<Announcement> announcements = new ArrayList<>();

        //TODO DB CALL
        Announcement announcement1 = new Announcement(
                "End of Semester Schedule",
                "Important dates for final exams and submissions. Please make note of all deadlines.",
                new Date(),
                "Dr. Smith",
                "Academic Coordinator"
        );
        announcement1.setRead(false);

        Announcement announcement2 = new Announcement(
                "Campus Maintenance Notice",
                "Library will be closed for maintenance this weekend.",
                new Date(System.currentTimeMillis() - 86400000), // Yesterday
                "Facility Management",
                "Admin"
        );
        announcement2.setRead(true);

        Announcement announcement3 = new Announcement(
                "New Course Registration",
                "Registration for next semester courses is now open.",
                new Date(System.currentTimeMillis() - 172800000), // 2 days ago
                "Registrar Office",
                "Administration"
        );
        announcement3.setRead(false);

        announcements.add(announcement1);
        announcements.add(announcement2);
        announcements.add(announcement3);

        return announcements;
    }

    private void updateAnnouncementsList(int tabPosition) {
        List<Announcement> displayList = tabPosition == 0 ? allAnnouncements : unreadAnnouncements;
        adapter.updateAnnouncements(displayList);


        if (displayList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
            emptyView.setText(tabPosition == 0 ? "No announcements yet" : "No unread announcements");
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }


    private class AnnouncementItemDecoration extends RecyclerView.ItemDecoration {
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            if (position != parent.getAdapter().getItemCount() - 1) {
                //outRect.bottom = getResources().getDimensionPixelSize(R.dimen.announcement_item_spacing);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}