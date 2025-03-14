package Student.Fragments;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import Student.Fragments.HomeFragment;
import Student.Fragments.ProfileFragment;
import Student.Fragments.CourseFragment;

public class FragmentAdapter extends FragmentPagerAdapter {
    public FragmentAdapter(FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    //    R.drawable.ic_home,
//    R.drawable.ic_courses,
//    R.drawable.ic_add,
//    R.drawable.ic_query,
//    R.drawable.ic_profile
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new Student.Fragments.HomeFragment();
            case 1:
                 return new CourseFragment();
            case 2:
                return new ProfileFragment();
            default:
                return new HomeFragment();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

}