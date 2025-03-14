package Student;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lms.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import Models.Query;

class QueryAdapter extends RecyclerView.Adapter<QueryAdapter.QueryViewHolder> {
    private List<Query> queries;
    private Context context;

    public QueryAdapter(Context context) {
        this.context = context;
        this.queries = new ArrayList<>();
    }

    @NonNull
    @Override
    public QueryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.query_item, parent, false);
        return new QueryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QueryViewHolder holder, int position) {
        Query query = queries.get(position);
        holder.bind(query);
    }

    @Override
    public int getItemCount() {
        return queries.size();
    }

    public void setQueries(List<Query> queries) {
        this.queries = queries;
        notifyDataSetChanged();
    }

    public void addQuery(Query query) {
        queries.add(0, query);
        notifyItemInserted(0);
    }

    class QueryViewHolder extends RecyclerView.ViewHolder {
        private TextView recipientName;
        private Chip statusChip;
        private TextView queryText;
        private TextView responseText;
        private TextView submissionDate;

        QueryViewHolder(@NonNull View itemView) {
            super(itemView);
            recipientName = itemView.findViewById(R.id.recipientName);
            statusChip = itemView.findViewById(R.id.statusChip);
            queryText = itemView.findViewById(R.id.queryText);
            responseText = itemView.findViewById(R.id.responseText);
            submissionDate = itemView.findViewById(R.id.submissionDate);
        }

        void bind(Query query) {
            recipientName.setText(query.getRecipient());
            queryText.setText(query.getQueryText());

            statusChip.setText(query.getStatus());
            //statusChip.setChipBackgroundColorResource(query.getStatus().getColorResId());

            if (query.getResponse() != null && !query.getResponse().isEmpty()) {
                responseText.setVisibility(View.VISIBLE);
                responseText.setText("Response: " + query.getResponse());
            } else {
                responseText.setVisibility(View.GONE);
            }

            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
            submissionDate.setText("Submitted: " + sdf.format(query.getSubmissionDate()));
        }
    }
}


public class QuerySub extends AppCompatActivity {
    private AutoCompleteTextView recipientDropdown;
    private TextInputEditText queryInput;
    private RecyclerView queriesRecyclerView;
    private QueryAdapter queryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query);


        recipientDropdown = findViewById(R.id.recipientDropdown);
        queryInput = findViewById(R.id.queryInput);
        queriesRecyclerView = findViewById(R.id.queriesRecyclerView);
        MaterialButton submitButton = findViewById(R.id.submitButton);


        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        queryAdapter = new QueryAdapter(this);
        queriesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        queriesRecyclerView.setAdapter(queryAdapter);


        setupRecipientDropdown();

        // Setup submit button
        submitButton.setOnClickListener(v -> submitQuery());


        loadQueries();
    }

    //TODO DB CALL
    private void setupRecipientDropdown() {
        String[] recipients = new String[] {
                "Mr. John (Mathematics)",
                "Mrs. Smith (English)",
                "Dr. Wilson (Admin)",
                "Ms. Johnson (Science)",
                "Mr. Brown (Admin)"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                recipients
        );
        recipientDropdown.setAdapter(adapter);
    }

    private void submitQuery() {
        String selectedRecipient = recipientDropdown.getText().toString();
        String queryText = queryInput.getText().toString().trim();

        if (selectedRecipient.isEmpty()) {
            showError("Please select a recipient");
            return;
        }

        if (queryText.isEmpty()) {
            showError("Please enter your query");
            return;
        }


        Query query = new Query(selectedRecipient, queryText);
        queryAdapter.addQuery(query);


        recipientDropdown.setText("");
        queryInput.setText("");


        Snackbar.make(
                findViewById(android.R.id.content),
                "Query submitted successfully",
                Snackbar.LENGTH_SHORT
        ).show();
    }

    private void loadQueries() {

        List<Query> sampleQueries = new ArrayList<>();
        //TODO DB CALL
        queryAdapter.setQueries(sampleQueries);
    }

    private void showError(String message) {
        Snackbar.make(
                findViewById(android.R.id.content),
                message,
                Snackbar.LENGTH_SHORT
        ).show();
    }
}