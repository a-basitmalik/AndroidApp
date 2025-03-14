package Instructor;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.example.lms.R;
import Models.Query;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class QueryList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_query_list);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });

        RecyclerView recyclerView = findViewById(R.id.queriesRecyclerView);
        QueriesAdapter adapter = new QueriesAdapter(this, query -> {
            // showResponseDialog(query);
        });


        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Add dummy data to the adapter
        adapter.setQueries(getDummyQueries());
    }

    //TODO DB CALL
    private List<Query> getDummyQueries() {
        List<Query> queries = new ArrayList<>();
        queries.add(new Query("John Doe", "What is the deadline for the project?"));
        queries.add(new Query("Jane Smith", "Can I get an extension on my assignment?"));
        queries.add(new Query("Mark Johnson", "Is attendance mandatory for the workshop?"));
        queries.add(new Query("Emily Davis", "Where can I find additional resources for the course?"));
        queries.add(new Query("Michael Brown", "Are there any sample papers available?"));


        Query respondedQuery = new Query("Sophia Wilson", "Can we reschedule the quiz?");
        respondedQuery.setResponse("The quiz has been rescheduled to next Monday.");
        queries.add(respondedQuery);

        return queries;
    }
}



class QueriesAdapter extends RecyclerView.Adapter<QueriesAdapter.QueryViewHolder> {
    private List<Query> queries = new ArrayList<>();
    private Context context;
    private OnQueryClickListener listener;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

    public interface OnQueryClickListener {
        void onQueryClick(Query query);
    }

    public QueriesAdapter(Context context, OnQueryClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public QueryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.query_list_item, parent, false);
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

    class QueryViewHolder extends RecyclerView.ViewHolder {
        private TextView recipientText;
        private TextView queryText;
        private TextView dateText;
        private TextView statusText;

        QueryViewHolder(@NonNull View itemView) {
            super(itemView);
            recipientText = itemView.findViewById(R.id.recipientText);
            queryText = itemView.findViewById(R.id.queryText);
            dateText = itemView.findViewById(R.id.dateText);
            statusText = itemView.findViewById(R.id.statusText);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onQueryClick(queries.get(position));
                }
            });
        }

        void bind(Query query) {
            recipientText.setText(query.getRecipient());
            queryText.setText(query.getQueryText());
            dateText.setText(dateFormat.format(query.getSubmissionDate()));

            statusText.setText(query.getStatus());
            //statusText.setBackgroundTintList(ContextCompat.getColorStateList(
              //      context, query.getStatus().getColorResId()));
        }
    }
}