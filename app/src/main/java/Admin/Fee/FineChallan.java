package Admin.Fee;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lms.R;
import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FineChallan extends AppCompatActivity {

    private String studentId;
    private String studentName;
    private double totalAmount;
    private int fineCount;
    private String fineDetails;
    private String challanNumber;
    private String challanDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fine_challan);

        // Get data from intent
        Intent intent = getIntent();
        studentId = intent.getStringExtra("studentId");
        studentName = intent.getStringExtra("studentName");
        totalAmount = intent.getDoubleExtra("totalAmount", 0.0);
        fineCount = intent.getIntExtra("fineCount", 0);
        fineDetails = intent.getStringExtra("fineDetails");
        challanNumber = intent.getStringExtra("challanNumber");

        // Set current date
        challanDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

        // Initialize UI elements
        TextView challanNumberText = findViewById(R.id.challanNumber);
        TextView challanDateText = findViewById(R.id.challanDate);
        TextView studentNameText = findViewById(R.id.studentName);
        TextView studentIdText = findViewById(R.id.studentId);
        TextView totalAmountText = findViewById(R.id.totalAmount);
        TextView fineCountText = findViewById(R.id.fineCount);
        TextView fineDetailsText = findViewById(R.id.fineDetails);

        MaterialButton printButton = findViewById(R.id.printButton);
        MaterialButton saveButton = findViewById(R.id.saveButton);
        MaterialButton shareButton = findViewById(R.id.shareButton);
        MaterialButton closeButton = findViewById(R.id.closeButton);

        // Set text values
        challanNumberText.setText(challanNumber);
        challanDateText.setText(challanDate);
        studentNameText.setText(studentName);
        studentIdText.setText(studentId);
        totalAmountText.setText(String.format("$%.2f", totalAmount));
        fineCountText.setText(String.valueOf(fineCount));
        fineDetailsText.setText(fineDetails);

        // Set click listeners
        printButton.setOnClickListener(v -> {
            // In a real app, implement print functionality here
            Toast.makeText(this, "Printing challan...", Toast.LENGTH_SHORT).show();
        });

        saveButton.setOnClickListener(v -> saveChallan());

        shareButton.setOnClickListener(v -> shareChallan());

        closeButton.setOnClickListener(v -> finish());
    }

    private void saveChallan() {
        StringBuilder content = new StringBuilder();
        content.append("Fine Challan\n\n");
        content.append("Challan Number: ").append(challanNumber).append("\n");
        content.append("Date: ").append(challanDate).append("\n\n");
        content.append("Student Name: ").append(studentName).append("\n");
        content.append("Student ID: ").append(studentId).append("\n\n");
        content.append("Fine Details:\n").append(fineDetails).append("\n");
        content.append("Total Amount: $").append(String.format("%.2f", totalAmount)).append("\n");
        content.append("Number of Fines: ").append(fineCount).append("\n\n");
        content.append("This challan must be paid within 7 days.");

        try {
            // In a real app, save to a more appropriate location with proper permissions
            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            File file = new File(path, challanNumber + ".txt");

            FileOutputStream stream = new FileOutputStream(file);
            stream.write(content.toString().getBytes());
            stream.close();

            Toast.makeText(this, "Challan saved to " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(this, "Error saving challan: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void shareChallan() {
        StringBuilder content = new StringBuilder();
        content.append("Fine Challan\n\n");
        content.append("Challan Number: ").append(challanNumber).append("\n");
        content.append("Date: ").append(challanDate).append("\n\n");
        content.append("Student Name: ").append(studentName).append("\n");
        content.append("Student ID: ").append(studentId).append("\n\n");
        content.append("Fine Details:\n").append(fineDetails).append("\n");
        content.append("Total Amount: $").append(String.format("%.2f", totalAmount)).append("\n");
        content.append("Number of Fines: ").append(fineCount).append("\n\n");
        content.append("This challan must be paid within 7 days.");

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Fine Challan " + challanNumber);
        shareIntent.putExtra(Intent.EXTRA_TEXT, content.toString());
        startActivity(Intent.createChooser(shareIntent, "Share Challan"));
    }
}