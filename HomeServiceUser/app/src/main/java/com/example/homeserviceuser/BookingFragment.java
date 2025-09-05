package com.example.homeserviceuser;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class BookingFragment extends Fragment {
    private AdView adView;

    private EditText searchInput;
    private TextView bookingsContainer;
    private ProgressBar progressBar;
    private Button btnFetchBooking;
    private FirebaseFirestore db;
    private static final String TAG = "BookingFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_booking, container, false);

        // Initialize Mobile Ads
        MobileAds.initialize(getContext(), initializationStatus -> { });

        // Load Ad
        adView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        // Initialize Firebase Firestore
        FirebaseApp.initializeApp(requireContext());
        db = FirebaseFirestore.getInstance();

        // Initialize UI elements
        searchInput = view.findViewById(R.id.searchInput);
        bookingsContainer = view.findViewById(R.id.bookingsContainer);
        progressBar = view.findViewById(R.id.progressBar);
        btnFetchBooking = view.findViewById(R.id.btnFetchBooking);

        // Set click listener on button
        btnFetchBooking.setOnClickListener(v -> {
            String input = searchInput.getText().toString().trim();
            if (!TextUtils.isEmpty(input)) {
                fetchBookings(input);
            } else {
                Toast.makeText(getContext(), "Please enter name and phone number", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void fetchBookings(String input) {
        Log.d(TAG, "Searching for bookings with input: " + input);

        progressBar.setVisibility(View.VISIBLE);
        bookingsContainer.setText("");

        db.collection("users")
                .whereEqualTo("name", input) // Search for exact match in the "name" field
                .get()
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);

                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            DocumentSnapshot doc = task.getResult().getDocuments().get(0); // Get the first matching document

                            String service = doc.getString("service");
                            String location = doc.getString("location");
                            String status = doc.getString("status");

                            String bookingDetails = "Name & Phone: " + input +
                                    "\nService: " + (service != null ? service : "N/A") +
                                    "\nLocation: " + (location != null ? location : "N/A") +
                                    "\nStatus: " + (status != null ? status : "N/A");

                            bookingsContainer.setText(bookingDetails);
                        } else {
                            bookingsContainer.setText("No matching details found.");
                        }
                    } else {
                        Exception e = task.getException();
                        bookingsContainer.setText("Error: " + (e != null ? e.getMessage() : "Unknown error"));
                        Log.e(TAG, "Firestore fetch failed: ", e);
                    }
                });
    }
}
