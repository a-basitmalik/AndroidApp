package Instructor.CourseFrags;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.lms.R;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Arrays;

import Admin.AdminSingleStudentView;

public class PeopleFragment extends Fragment {
    private ArrayAdapter<String> adapter;
    private String[] allNames;
    private ArrayList<String> filteredList;

    private View rootView;

    public static PeopleFragment newInstance(String type) {
        PeopleFragment fragment = new PeopleFragment();
        Bundle args = new Bundle();
        args.putString("type", type);
        fragment.setArguments(args);
        return fragment;
    }

    private String[] getStudentNames() {
        // db call TODO:
        return new String[]{"Sara", "M", "A"};
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_shared_list, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
       // setupWindowInsets();
        initializeList();
        loadList();
    }
//
//    private void setupWindowInsets() {
//        ViewCompat.setOnApplyWindowInsetsListener(rootView.findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//    }

    private void initializeList() {

            allNames = getStudentNames();
        filteredList = new ArrayList<>(Arrays.asList(allNames));
    }



    private void loadList() {
        ListView listView = rootView.findViewById(R.id.studentListView);
        TextInputEditText searchEditText = rootView.findViewById(R.id.searchEditText);

        adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                filteredList
        );
        listView.setAdapter(adapter);

        setupSearchListener(searchEditText);
        setupItemClickListener(listView);
    }

    private void setupSearchListener(TextInputEditText searchEditText) {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                filterItems(s.toString());
            }
        });
    }

    private void setupItemClickListener(ListView listView) {
        listView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedItem = filteredList.get(position);

            Intent intent;

                intent = new Intent(requireContext(), AdminSingleStudentView.class);
                intent.putExtra("selectedStudentKey", selectedItem);


            startActivity(intent);
        });
    }

    private void filterItems(String searchText) {
        filteredList.clear();
        if (searchText.isEmpty()) {
            filteredList.addAll(Arrays.asList(allNames));
        } else {
            String searchLower = searchText.toLowerCase().trim();
            for (String item : allNames) {
                if (item.toLowerCase().contains(searchLower)) {
                    filteredList.add(item);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
}

