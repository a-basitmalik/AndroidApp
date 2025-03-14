package Admin.Fee;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lms.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import androidx.core.util.Pair;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import Models.Payment;
public class TrackPayment extends AppCompatActivity {
    private RecyclerView recyclerView;
    private PaymentHistoryAdapter adapter;
    private ProgressBar progressBar;
    private TextInputEditText searchInput;
    private TextInputEditText dateFromInput;
    private TextInputEditText dateToInput;
    private ImageButton btnExport;

    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private Date fromDate = null;
    private Date toDate = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_payment);

        initializeViews();
        setupToolbar();
        setupRecyclerView();
        setupSearchListener();
        setupDatePickers();
        setupExportButton();


        loadPaymentHistory();
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.paymentHistoryRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        searchInput = findViewById(R.id.searchInput);
        dateFromInput = findViewById(R.id.dateFromInput);
        dateToInput = findViewById(R.id.dateToInput);
        btnExport = findViewById(R.id.btnExport);
    }


    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new PaymentHistoryAdapter(new ArrayList<>());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setupSearchListener() {
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                filterPaymentHistory();
            }
        });
    }

    private void setupDatePickers() {
        dateFromInput.setOnClickListener(v -> showDateRangePicker());
        dateToInput.setOnClickListener(v -> showDateRangePicker());
    }

    private void showDateRangePicker() {
        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
        MaterialDatePicker<Pair<Long, Long>> picker = builder.build();

        picker.addOnPositiveButtonClickListener(selection -> {
            fromDate = new Date(selection.first);
            toDate = new Date(selection.second);

            dateFromInput.setText(dateFormatter.format(fromDate));
            dateToInput.setText(dateFormatter.format(toDate));

            filterPaymentHistory();
        });

        picker.show(getSupportFragmentManager(), picker.toString());
    }

    private void setupExportButton() {
        btnExport.setOnClickListener(v -> {
            // TODO: Implement CSV export functionality
        });
    }

    private void loadPaymentHistory() {
        showLoading(true);

        // TODO: DB CALL

        new Thread(() -> {
            List<Payment> payments = getDummyPaymentRecords();
            runOnUiThread(() -> {
                adapter.setPayments(payments);
                showLoading(false);
            });
        }).start();
    }

    private void filterPaymentHistory() {
        String searchQuery = searchInput.getText().toString().toLowerCase();
        List<Payment> filteredList = new ArrayList<>();

        for (Payment payment : adapter.getOriginalPayments()) {
            boolean matchesSearch = searchQuery.isEmpty() ||
                    payment.getStudentName().toLowerCase().contains(searchQuery) ||
                    payment.getStudentId().toLowerCase().contains(searchQuery);

            boolean matchesDate = true;
            if (fromDate != null && toDate != null) {
                matchesDate = payment.getPaymentDate().after(fromDate) &&
                        payment.getPaymentDate().before(toDate);
            }

            if (matchesSearch && matchesDate) {
                filteredList.add(payment);
            }
        }

        adapter.setPayments(filteredList);
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
    }


    // TODO: DB CALL
    private List<Payment> getDummyPaymentRecords() {
        List<Payment> records = new ArrayList<>();
        Random random = new Random();
        Calendar calendar = Calendar.getInstance();

        // Sample student names
        String[] studentNames = {
                "John Smith", "Emma Johnson", "Michael Brown", "Sarah Davis",
                "James Wilson", "Emily Taylor", "William Anderson", "Olivia Martinez",
                "Alexander Thomas", "Sophia Garcia"
        };

        // Sample payment statuses
        String[] statuses = {
                "Completed", "Pending", "Failed", "Processing"
        };

        // Generate 20 random payment records
        for (int i = 0; i < 20; i++) {
            // Generate random student ID (e.g., STU1001)
            String studentId = String.format("STU%04d", 1001 + i);

            // Random amount between $50 and $500
            double amount = 50 + random.nextDouble() * 450;

            // Random date within the last 30 days
            calendar.setTime(new Date());
            calendar.add(Calendar.DAY_OF_YEAR, -random.nextInt(30));
            Date paymentDate = calendar.getTime();

            // Random student name and status
            String studentName = studentNames[random.nextInt(studentNames.length)];
            String status = statuses[random.nextInt(statuses.length)];

            records.add(new Payment(
                    studentName,
                    studentId,
                    amount,
                    paymentDate,
                    status
            ));
        }

        return records;
    }
}

class PaymentHistoryAdapter extends RecyclerView.Adapter<PaymentHistoryAdapter.PaymentViewHolder> {
    private List<Payment> payments;
    private List<Payment> originalPayments;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    public PaymentHistoryAdapter(List<Payment> payments) {
        this.payments = payments;
        this.originalPayments = payments;
    }

    @NonNull
    @Override
    public PaymentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.payment_his_item, parent, false);
        return new PaymentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentViewHolder holder, int position) {
        Payment payment = payments.get(position);
        holder.bind(payment);
    }

    @Override
    public int getItemCount() {
        return payments.size();
    }

    public void setPayments(List<Payment> payments) {
        this.payments = payments;
        this.originalPayments = payments;
        notifyDataSetChanged();
    }

    public List<Payment> getOriginalPayments() {
        return originalPayments;
    }

    static class PaymentViewHolder extends RecyclerView.ViewHolder {
        private final TextView studentNameText;
        private final TextView studentIdText;
        private final TextView amountText;
        private final TextView dateText;
        private final TextView statusText;

        public PaymentViewHolder(@NonNull View itemView) {
            super(itemView);
            studentNameText = itemView.findViewById(R.id.studentNameText);
            studentIdText = itemView.findViewById(R.id.studentIdText);
            amountText = itemView.findViewById(R.id.amountText);
            dateText = itemView.findViewById(R.id.dateText);
            statusText = itemView.findViewById(R.id.statusText);
        }

        public void bind(Payment payment) {
            studentNameText.setText(payment.getStudentName());
            studentIdText.setText(payment.getStudentId());
            amountText.setText(String.format("Rs%.2f", payment.getAmount()));
            dateText.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    .format(payment.getPaymentDate()));
            statusText.setText(payment.getStatus());
        }
    }
}