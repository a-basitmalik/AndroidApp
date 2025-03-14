package Admin.Fee;


import com.example.lms.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.card.MaterialCardView;


public class FeeDashboard extends AppCompatActivity {

    private TextView totalCollectionsText;
    private MaterialCardView fineCard, trackPaymentCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fee_dashboard);


        initializeViews();
        setupToolbar();
        setupClickListeners();
        loadFeeData();
    }

    private void initializeViews() {
        totalCollectionsText = findViewById(R.id.totalCollections);
        fineCard = findViewById(R.id.fineCard);
        trackPaymentCard = findViewById(R.id.trackPaymentCard);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void setupClickListeners() {
        fineCard.setOnClickListener(v -> {
            Intent intent = new Intent(this, FineList.class);
            startActivity(intent);


        });

        trackPaymentCard.setOnClickListener(v -> {
            Intent intent = new Intent(this, TrackPayment.class);
            startActivity(intent);

        });
    }

    private void loadFeeData() {
        // TODO: DB CALL
        updateTotalCollections("1,25,000");
    }

    private void updateTotalCollections(String amount) {
        totalCollectionsText.setText(amount);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        //overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}