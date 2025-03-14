package Instructor;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.lms.R;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import Models.Assignment;
public class CreateAssignment extends AppCompatActivity {

    private TextInputEditText titleEditText;
    private TextInputEditText descriptionEditText;
    private TextInputEditText dueDateEditText;
    private TextInputEditText instructionsEditText;

    private MaterialButton createButton;
    private TextView selectedFilesText;
    private LinearLayout uploadLayout;
    private ImageView uploadIcon;

    private List<Uri> selectedFiles = new ArrayList<>();
    private Calendar calendar = Calendar.getInstance();

    private final ActivityResultLauncher<String[]> filePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.OpenMultipleDocuments(),
            uris -> {
                if (uris != null && !uris.isEmpty()) {
                    selectedFiles.addAll(uris);
                    updateSelectedFilesText();

                    uploadLayout.setVisibility(View.GONE);
                    selectedFilesText.setVisibility(View.VISIBLE);
                }
            }
    );

    private void showRemoveFileDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Remove Files")
                .setMessage("Do you want to remove all selected files?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    selectedFiles.clear();
                    selectedFilesText.setVisibility(View.GONE);
                    uploadLayout.setVisibility(View.VISIBLE);
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_assignment);

        initializeViews();
        setupListeners();
    }
    private void initializeViews() {
        titleEditText = findViewById(R.id.titleEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        dueDateEditText = findViewById(R.id.dueDateEditText);
        instructionsEditText = findViewById(R.id.instructionsEditText);

        createButton = findViewById(R.id.createButton);
        selectedFilesText = findViewById(R.id.selectedFilesText);
        uploadLayout = findViewById(R.id.uploadLayout); // Add this ID to your XML
        uploadIcon = findViewById(R.id.uploadIcon); // Add this ID to your ImageView

        // Initially hide the selected files text
        selectedFilesText.setVisibility(View.GONE);
    }
    private void setupListeners() {
        dueDateEditText.setOnClickListener(v -> showDatePicker());



        uploadLayout.setOnClickListener(v -> launchFilePicker());

        createButton.setOnClickListener(v -> createAssignment());
    }
    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateDateText();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void updateDateText() {
        String dateFormat = "MM/dd/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
        dueDateEditText.setText(sdf.format(calendar.getTime()));
    }

    private void updateSelectedFilesText() {
        if (!selectedFiles.isEmpty()) {
            StringBuilder fileNames = new StringBuilder("Selected files:\n");
            for (Uri uri : selectedFiles) {
                String fileName = getFileName(uri);
                fileNames.append("- ").append(fileName).append("\n");
            }
            selectedFilesText.setText(fileNames.toString());

            // remove option
            selectedFilesText.setOnClickListener(v -> showRemoveFileDialog());
        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex != -1) {
                        result = cursor.getString(nameIndex);
                    }
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
    private void createAssignment() {
        String title = titleEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String dueDate = dueDateEditText.getText().toString().trim();
        String instructions = instructionsEditText.getText().toString().trim();

        if (validateInputs(title, description, dueDate, instructions)) {
            // Create assignment object and upload files
            Assignment assignment = new Assignment(
                    title,
                    description,
                    calendar.getTime(),
                    instructions
            );

            uploadAssignmentAndFiles(assignment);
        }
    }

    private boolean validateInputs(String title, String description, String dueDate, String instructions) {
        if (title.isEmpty()) {
            titleEditText.setError("Title is required");
            return false;
        }
        if (description.isEmpty()) {
            descriptionEditText.setError("Description is required");
            return false;
        }
        if (dueDate.isEmpty()) {
            dueDateEditText.setError("Due date is required");
            return false;
        }
        if (instructions.isEmpty()) {
            instructionsEditText.setError("Instructions are required");
            return false;
        }
        return true;
    }

    private void launchFilePicker() {
        String[] mimeTypes = {
                "application/pdf",
                "application/msword",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        };
        filePickerLauncher.launch(mimeTypes);
    }

    private void uploadAssignmentAndFiles(Assignment assignment) {
        // Show progress dialog
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Creating assignment...");
        progressDialog.setCancelable(false);
        progressDialog.show();


        for (Uri fileUri : selectedFiles) {

            String fileName = getFileName(fileUri);
            //TODO DB CALL -> upload
            assignment.addAttachment("https://your-server.com/files/" + fileName);
        }

        //  TODO DB CALL implement your actual assignment upload logic
        new Handler().postDelayed(() -> {
            progressDialog.dismiss();
            Toast.makeText(this, "Assignment created successfully", Toast.LENGTH_SHORT).show();
            finish();
        }, 2000);
    }

}