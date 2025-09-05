package com.example.homeserviceprovider;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class BookingsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private FirebaseFirestore db;
    private SharedPreferences sharedPreferences;
    private List<Booking> bookingList;
    private BookingAdapter adapter;
    private static final String PREF_NAME = "ServiceProviderPrefs";
    private static final String KEY_PHONE = "phone";
    private String phone;
    private static final String TAG = "BookingsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookings);

        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();

        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        phone = sharedPreferences.getString(KEY_PHONE, "");

        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        bookingList = new ArrayList<>();
        adapter = new BookingAdapter(this, bookingList);
        recyclerView.setAdapter(adapter);

        fetchBookings();
    }

    private void fetchBookings() {
        progressBar.setVisibility(View.VISIBLE);

        db.collection("bookings").document(phone).collection("requests")
                .whereEqualTo("status", "pending").get().addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);

                    if (task.isSuccessful() && task.getResult() != null) {
                        bookingList.clear();
                        for (DocumentSnapshot doc : task.getResult()) {
                            bookingList.add(doc.toObject(Booking.class));
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}
