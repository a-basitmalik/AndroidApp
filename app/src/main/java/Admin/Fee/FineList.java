package Admin.Fee;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lms.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import Models.Fine;

public class FineList extends AppCompatActivity implements FineAdapter.OnFineWaiverClickListener,
        FineAdapter.OnFineRemoveClickListener,
        FineAdapter.OnFineChallanClickListener {

    private RecyclerView fineRecyclerView;
    private FineAdapter adapter;
    private List<Fine> fines;
    private TextInputEditText searchEditText;
    private ExtendedFloatingActionButton addFineFab;
    private MaterialButton btnGenerateReport;

    private final String[] fineTypes = {"Absentee Fine", "Exam leaving Fine", "Uniform Fine", "Late Fine", "Custom Fine"};
    private double[] defaultFineAmounts = {100.00, 500.00, 500.00, 100.00, 0.00};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fine_list);


        fineRecyclerView = findViewById(R.id.fineRecyclerView);
        searchEditText = findViewById(R.id.searchEditText);
        addFineFab = findViewById(R.id.addFineFab);
        btnGenerateReport = findViewById(R.id.btnGenerateReport);


        fines = new ArrayList<>();
        adapter = new FineAdapter(fines, this, this, this);
        fineRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        fineRecyclerView.setAdapter(adapter);


        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });


        addFineFab.setOnClickListener(v -> showAddFineDialog());
        btnGenerateReport.setOnClickListener(v -> handleGenerateReport());
    }

    private void handleGenerateReport() {
        List<String> studentsWithSelectedFines = adapter.getStudentsWithSelectedFines();

        if (studentsWithSelectedFines.isEmpty()) {
            Toast.makeText(this, "Please select at least one fine", Toast.LENGTH_SHORT).show();
            return;
        }

        if (studentsWithSelectedFines.size() > 1) {
            showSelectStudentDialog(studentsWithSelectedFines);
        } else {
            String studentId = studentsWithSelectedFines.get(0);
            List<Fine> selectedFines = adapter.getSelectedFinesForStudent(studentId);
            generateChallanForStudent(studentId, selectedFines);
        }
    }

    private void showSelectStudentDialog(List<String> studentIds) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Student for Challan");

        Map<String, String> studentNames = adapter.getStudentNames();
        String[] displayNames = new String[studentIds.size()];

        for (int i = 0; i < studentIds.size(); i++) {
            String id = studentIds.get(i);
            displayNames[i] = studentNames.getOrDefault(id, "Unknown") + " (" + id + ")";
        }

        builder.setItems(displayNames, (dialog, which) -> {
            String selectedStudentId = studentIds.get(which);
            List<Fine> selectedFines = adapter.getSelectedFinesForStudent(selectedStudentId);
            generateChallanForStudent(selectedStudentId, selectedFines);
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void generateChallanForStudent(String studentId, List<Fine> selectedFines) {
        if (selectedFines.isEmpty()) {
            Toast.makeText(this, "No fines selected for this student", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, FineChallan.class);
        intent.putExtra("studentId", studentId);
        intent.putExtra("studentName", adapter.getStudentNames().get(studentId));

        double totalAmount = 0;
        for (Fine fine : selectedFines) {
            totalAmount += fine.getAmount();
        }

        intent.putExtra("totalAmount", totalAmount);
        intent.putExtra("fineCount", selectedFines.size());

        StringBuilder details = new StringBuilder();
        for (Fine fine : selectedFines) {
            details.append(fine.getType())
                    .append(": $")
                    .append(String.format("%.2f", fine.getAmount()))
                    .append("\n");
        }
        intent.putExtra("fineDetails", details.toString());

        String date = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
        String challanNumber = "FINE-" + date + "-" + studentId;
        intent.putExtra("challanNumber", challanNumber);

        startActivity(intent);
        adapter.clearSelections();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.fine_menu, menu);
        menu.add(0, 1, 0, "Add Dummy Data");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 1) {
            addDummyFines();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addDummyFines() {
        String[] studentNames = {"John Smith", "Emma Johnson", "Michael Brown", "Olivia Davis"};
        String[] studentIds = {"ST001", "ST002", "ST003", "ST004"};

        Random random = new Random();

        for (int i = 0; i < 20; i++) {
            int studentIndex = random.nextInt(studentNames.length);
            int fineTypeIndex = random.nextInt(fineTypes.length - 1);

            double baseAmount = defaultFineAmounts[fineTypeIndex];
            double variation = (random.nextDouble() * 2 - 1) * (baseAmount * 0.1);
            double amount = Math.max(0, baseAmount + variation);

            Fine fine = new Fine(studentNames[studentIndex], studentIds[studentIndex], amount, fineTypes[fineTypeIndex]);

            if (random.nextDouble() < 0.2) {
                fine.setWaived(true);
            }

            fines.add(fine);
        }

        for (int i = 0; i < 5; i++) {
            int studentIndex = random.nextInt(studentNames.length);
            double customAmount = 5 + (random.nextDouble() * 45);

            Fine customFine = new Fine(
                    studentNames[studentIndex],
                    studentIds[studentIndex],
                    customAmount,
                    "Custom Fine: " + getRandomCustomReason(random)
            );

            fines.add(customFine);
        }

        adapter.filter(searchEditText.getText().toString());
        adapter.notifyDataSetChanged();
        Toast.makeText(this, "Added 25 dummy fines", Toast.LENGTH_SHORT).show();
    }

    private String getRandomCustomReason(Random random) {
        String[] reasons = {
                "Library book damage", "Lab equipment damage", "Graffiti",
                "Cafeteria violation", "Parking violation"
        };
        return reasons[random.nextInt(reasons.length)];
    }

    private void showAddFineDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.add_fine_dialog, null);
        builder.setView(dialogView);
        builder.setTitle("Add Fine");

        TextInputLayout studentNameLayout = dialogView.findViewById(R.id.studentNameLayout);
        TextInputLayout studentIdLayout = dialogView.findViewById(R.id.studentIdLayout);
        TextInputLayout amountLayout = dialogView.findViewById(R.id.amountLayout);
        TextInputEditText studentNameInput = dialogView.findViewById(R.id.studentNameInput);
        TextInputEditText studentIdInput = dialogView.findViewById(R.id.studentIdInput);
        TextInputEditText amountInput = dialogView.findViewById(R.id.amountInput);
        Spinner fineTypeSpinner = dialogView.findViewById(R.id.fineTypeSpinner);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, fineTypes);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fineTypeSpinner.setAdapter(spinnerAdapter);

        fineTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position < defaultFineAmounts.length) {
                    amountInput.setText(String.format("%.2f", defaultFineAmounts[position]));
                    amountInput.setEnabled(position == fineTypes.length - 1);
                    if (position == fineTypes.length - 1) {
                        amountInput.requestFocus();
                        amountInput.selectAll();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        AlertDialog dialog = builder.setPositiveButton("Add", null)
                .setNegativeButton("Cancel", null)
                .create();

        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String studentName = studentNameInput.getText().toString().trim();
            String studentId = studentIdInput.getText().toString().trim();
            String amountStr = amountInput.getText().toString().trim();
            String fineType = fineTypes[fineTypeSpinner.getSelectedItemPosition()];

            boolean isValid = true;

            if (studentName.isEmpty()) {
                studentNameLayout.setError("Student name is required");
                isValid = false;
            } else {
                studentNameLayout.setError(null);
            }

            if (studentId.isEmpty()) {
                studentIdLayout.setError("Student ID is required");
                isValid = false;
            } else {
                studentIdLayout.setError(null);
            }

            if (amountStr.isEmpty()) {
                amountLayout.setError("Amount is required");
                isValid = false;
            } else {
                amountLayout.setError(null);
            }

            if (isValid) {
                double amount = Double.parseDouble(amountStr);
                fines.add(new Fine(studentName, studentId, amount, fineType));
                adapter.filter(searchEditText.getText().toString());
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onWaiverClick(Fine fine, int position) {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Waiver")
                .setMessage("Waive this fine?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    fine.setWaived(true);
                    adapter.notifyItemChanged(position);
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public void onRemoveClick(Fine fine, int position) {
        new AlertDialog.Builder(this)
                .setTitle("Remove Fine")
                .setMessage("Remove this fine?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    fines.remove(fine);
                    adapter.filter(searchEditText.getText().toString());
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public void onChallanClick(List<Fine> selectedFines) {
        // Implemented via generate report button
    }
}

class FineAdapter extends RecyclerView.Adapter<FineAdapter.FineViewHolder> {
    private List<Fine> fines;
    private List<Fine> filteredFines;
    private OnFineWaiverClickListener waiverClickListener;
    private OnFineRemoveClickListener removeClickListener;
    private OnFineChallanClickListener challanClickListener;
    private Map<String, Boolean> selectedFines;



    public interface OnFineWaiverClickListener {
        void onWaiverClick(Fine fine, int position);
    }

    public interface OnFineRemoveClickListener {
        void onRemoveClick(Fine fine, int position);
    }

    public interface OnFineChallanClickListener {
        void onChallanClick(List<Fine> selectedFines);
    }

    public FineAdapter(List<Fine> fines, OnFineWaiverClickListener waiverListener,
                       OnFineRemoveClickListener removeListener,
                       OnFineChallanClickListener challanListener) {
        this.fines = fines;
        this.filteredFines = new ArrayList<>(fines);
        this.waiverClickListener = waiverListener;
        this.removeClickListener = removeListener;
        this.challanClickListener = challanListener;
        this.selectedFines = new HashMap<>();
    }

    @NonNull
    @Override
    public FineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fine_item, parent, false);
        return new FineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FineViewHolder holder, int position) {
        Fine fine = filteredFines.get(position);
        String fineId = fine.getStudentId() + "_" + position;
        boolean isSelected = selectedFines.containsKey(fineId) && selectedFines.get(fineId);
        holder.bind(fine, position, isSelected);
    }

    @Override
    public int getItemCount() {
        return filteredFines.size();
    }

    public void filter(String query) {
        filteredFines.clear();
        if (query.isEmpty()) {
            filteredFines.addAll(fines);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (Fine fine : fines) {
                if (fine.getStudentName().toLowerCase().contains(lowerCaseQuery) ||
                        fine.getStudentId().toLowerCase().contains(lowerCaseQuery) ||
                        fine.getType().toLowerCase().contains(lowerCaseQuery)) {
                    filteredFines.add(fine);
                }
            }
        }
        notifyDataSetChanged();
    }

    public List<Fine> getSelectedFinesForStudent(String studentId) {
        List<Fine> selected = new ArrayList<>();
        for (int i = 0; i < filteredFines.size(); i++) {
            Fine fine = filteredFines.get(i);
            String fineId = fine.getStudentId() + "_" + i;
            if (fine.getStudentId().equals(studentId) &&
                    selectedFines.containsKey(fineId) &&
                    selectedFines.get(fineId)) {
                selected.add(fine);
            }
        }
        return selected;
    }

    public List<String> getStudentsWithSelectedFines() {
        List<String> students = new ArrayList<>();
        Map<String, Boolean> added = new HashMap<>();
        for (int i = 0; i < filteredFines.size(); i++) {
            Fine fine = filteredFines.get(i);
            String fineId = fine.getStudentId() + "_" + i;
            if (selectedFines.containsKey(fineId) &&
                    selectedFines.get(fineId) &&
                    !added.containsKey(fine.getStudentId())) {
                students.add(fine.getStudentId());
                added.put(fine.getStudentId(), true);
            }
        }
        return students;
    }

    public Map<String, String> getStudentNames() {
        Map<String, String> names = new HashMap<>();
        for (Fine fine : filteredFines) {
            names.put(fine.getStudentId(), fine.getStudentName());
        }
        return names;
    }

    public void clearSelections() {
        selectedFines.clear();
        notifyDataSetChanged();
    }

    class FineViewHolder extends RecyclerView.ViewHolder {
        private TextView studentName, studentId, fineAmount, fineType;
        private MaterialButton waiverButton, removeButton;

        FineViewHolder(@NonNull View itemView) {
            super(itemView);
            studentName = itemView.findViewById(R.id.studentName);
            studentId = itemView.findViewById(R.id.studentId);
            fineAmount = itemView.findViewById(R.id.fineAmount);
            fineType = itemView.findViewById(R.id.fineType);
            waiverButton = itemView.findViewById(R.id.waiverButton);
            removeButton = itemView.findViewById(R.id.removeButton);
        }

        void bind(Fine fine, int position, boolean isSelected) {
            studentName.setText(fine.getStudentName());
            studentId.setText(fine.getStudentId());
            fineAmount.setText(String.format("$%.2f", fine.getAmount()));
            fineType.setText(fine.getType());

            waiverButton.setEnabled(!fine.isWaived());
            waiverButton.setText(fine.isWaived() ? "Waived" : "Waiver");

            waiverButton.setOnClickListener(v -> {
                if (waiverClickListener != null) {
                    waiverClickListener.onWaiverClick(fine, position);
                }
            });

            removeButton.setOnClickListener(v -> {
                if (removeClickListener != null) {
                    removeClickListener.onRemoveClick(fine, position);
                }
            });

            itemView.setOnClickListener(v -> {
                String fineId = fine.getStudentId() + "_" + position;
                boolean newState = !(selectedFines.containsKey(fineId) && selectedFines.get(fineId));
                selectedFines.put(fineId, newState);
                notifyItemChanged(position);
            });
        }
    }
}