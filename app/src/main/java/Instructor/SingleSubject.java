package Instructor;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.lms.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import Instructor.CourseFrags.ClassworkFragment;
import Instructor.CourseFrags.GeneralFragment;
import Instructor.CourseFrags.PeopleFragment;


public class SingleSubject extends AppCompatActivity {
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private ViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_subject);

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);

        adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("General");
                            break;
                        case 1:
                            tab.setText("People");
                            break;
                        case 2:
                            tab.setText("Classwork");
                            break;
                    }
                }
        ).attach();
    }
}
class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
               return new GeneralFragment();
            case 1:
               return new PeopleFragment();
            case 2:
                return new ClassworkFragment();
            default:
                return new GeneralFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}



