package Instructor;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lms.R;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import Models.Query;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class QueryResponse extends AppCompatActivity {
    private Query query;
    private TextInputEditText responseInput;
    private MaterialButton attachFileButton;
    private MaterialButton attachLinkButton;
    private MaterialButton submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_response);


        query = (Query) getIntent().getSerializableExtra("query");
        if (query == null) {
            Toast.makeText(this, "Error loading query", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setupToolbar();
        initializeViews();
        setupListeners();
        populateQueryDetails();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Respond to Query");
    }

    private void initializeViews() {
        responseInput = findViewById(R.id.responseInput);
        attachFileButton = findViewById(R.id.attachFileButton);
        attachLinkButton = findViewById(R.id.attachLinkButton);
        submitButton = findViewById(R.id.submitButton);
    }

    private void setupListeners() {
        attachFileButton.setOnClickListener(v -> {
            //todo
            Toast.makeText(this, "File attachment coming soon", Toast.LENGTH_SHORT).show();
        });

        attachLinkButton.setOnClickListener(v -> {

            Toast.makeText(this, "Link attachment coming soon", Toast.LENGTH_SHORT).show();
        });

        submitButton.setOnClickListener(v -> handleSubmission());
    }

    private void populateQueryDetails() {
        TextView queryText = findViewById(R.id.queryText);
        TextView studentName = findViewById(R.id.studentName);
        TextView submissionDate = findViewById(R.id.submissionDate);

        queryText.setText(query.getQueryText());
        studentName.setText(query.getRecipient());

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        submissionDate.setText(dateFormat.format(query.getSubmissionDate()));


        if (query.getResponse() != null) {
            responseInput.setText(query.getResponse());
        }
    }

    private void handleSubmission() {
        String response = responseInput.getText().toString().trim();
        if (response.isEmpty()) {
            responseInput.setError("Response cannot be empty");
            return;
        }

        // TODO DB CALL ->upload
        query.setResponse(response);


        Toast.makeText(this, "Response submitted successfully", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}