package Admin;
import com.android.volley.DefaultRetryPolicy;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.lms.R;
import com.google.android.material.card.MaterialCardView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Shared.SharedList;

public class selectCampus extends AppCompatActivity {

    private static final String BASE_URL = "http://193.203.162.232:5050";  // Replace with your Flask server IP

    private void createCampusCards(List<JSONObject> campuses) {
        LinearLayout buttonContainer = findViewById(R.id.campusButtonContainer);

        for (JSONObject campus : campuses) {
            try {
                final int campusID = campus.getInt("CampusID"); // ✅ Extract CampusID
                String campusName = campus.getString("CampusName");

                MaterialCardView cardView = new MaterialCardView(this);
                LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                cardParams.setMargins(0, 0, 0, dpToPx(16));
                cardView.setLayoutParams(cardParams);
                cardView.setRadius(dpToPx(16));
                cardView.setElevation(dpToPx(4));
                cardView.setCardBackgroundColor(getColor(R.color.background_secondary));
                cardView.setClickable(true);
                cardView.setFocusable(true);
                cardView.setStrokeWidth(dpToPx(1));

                LinearLayout contentLayout = new LinearLayout(this);
                contentLayout.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ));
                contentLayout.setOrientation(LinearLayout.HORIZONTAL);
                contentLayout.setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16));

                LinearLayout textLayout = new LinearLayout(this);
                LinearLayout.LayoutParams textLayoutParams = new LinearLayout.LayoutParams(
                        0, LinearLayout.LayoutParams.WRAP_CONTENT, 1
                );
                textLayoutParams.setMarginStart(dpToPx(16));
                textLayout.setLayoutParams(textLayoutParams);
                textLayout.setOrientation(LinearLayout.VERTICAL);
                textLayout.setGravity(Gravity.CENTER_VERTICAL);

                //-----------------------------------Name-----------------------------------
                TextView campusNameView = new TextView(this);
                campusNameView.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ));
                campusNameView.setText(campusName);
                campusNameView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                campusNameView.setTextColor(getColor(R.color.text_primary));


                //-----------------------------------Arrow-----------------------------------
                ImageView arrowView = new ImageView(this);
                LinearLayout.LayoutParams arrowParams = new LinearLayout.LayoutParams(
                        dpToPx(24), dpToPx(24)
                );
                arrowView.setLayoutParams(arrowParams);
                arrowView.setImageResource(R.drawable.ic_arrow);
                arrowView.setColorFilter(getColor(R.color.text_primary));

                textLayout.addView(campusNameView);
                contentLayout.addView(textLayout);
                contentLayout.addView(arrowView);

                cardView.addView(contentLayout);

                cardView.setOnClickListener(v -> {
                    Intent intent = new Intent(selectCampus.this, AdminDashboard.class);
                    intent.putExtra("campusID", campusID);
                    intent.putExtra("campusName", campusName);
                    startActivity(intent);

                });

                buttonContainer.addView(cardView);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadCampusesFromDatabase() {
        String url = BASE_URL + "/campus/get_campuses";  // Flask API Endpoint

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        List<JSONObject> campuses = new ArrayList<>();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                campuses.add(response.getJSONObject(i));
                            }
                            createCampusCards(campuses);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(selectCampus.this, "Error parsing JSON", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse != null) {
                    Log.e("Volley", "Error Code: " + error.networkResponse.statusCode);
                    Log.e("Volley", "Response Data: " + new String(error.networkResponse.data));
                } else {
                    Log.e("Volley", "Volley Error: " + error.toString());
                }
                Toast.makeText(selectCampus.this, "Failed to load campuses", Toast.LENGTH_LONG).show();
            }
        });

        // 🔹 **Set Timeout to 10 Seconds (10000 ms)**
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,  // Timeout in milliseconds
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,  // Default retry count
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT  // Default backoff multiplier
        ));

        queue.add(jsonArrayRequest);
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_select_campus);
        loadCampusesFromDatabase();
    }
}
