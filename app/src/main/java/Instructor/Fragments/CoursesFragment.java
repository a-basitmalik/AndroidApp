package Instructor.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import com.example.lms.R;

import Instructor.SingleSubject;


public class CoursesFragment extends Fragment {

    //TODO DB CALL
    public String[][] getCourseDetails() {
        String[][] courseDetails;

        courseDetails = new String[][]{
                {"OOP", "Object-Oriented\nProgramming"},
                {"Probability", "Statistics &\nProbability"}
        };

        return courseDetails;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_course_stu, container, false);


        String[][] courseDetails = getCourseDetails();

        GridLayout gridLayout = rootView.findViewById(R.id.courses_grid);
        TextView headerText = rootView.findViewById(R.id.header);

        Context context = requireContext();


        headerText.setTextColor(ContextCompat.getColor(context, R.color.text_primary));


        DisplayMetrics displayMetrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int buttonWidth = (screenWidth - (16 * 10)) / 2;
        int buttonHeight = buttonWidth;


        for (String[] course : courseDetails) {
            Button button = new Button(context);
            button.setText(course[1]);


            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = buttonWidth;
            params.height = buttonHeight;
            params.setMargins(16, 16, 16, 16);
            button.setLayoutParams(params);


            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            drawable.setColor(ContextCompat.getColor(context, R.color.primary_color_dark));
            drawable.setCornerRadius(32f);

            button.setBackground(drawable);
            button.setTextColor(Color.WHITE);
            button.setAllCaps(false);

            button.setClickable(true);
            button.setFocusable(true);


            button.setOnClickListener(v -> {
                Intent intent;
                intent = new Intent(context, SingleSubject.class);
                intent.putExtra("course_title", course[0]);
                startActivity(intent);
            });

            gridLayout.addView(button);
        }

        return rootView;
    }
}

