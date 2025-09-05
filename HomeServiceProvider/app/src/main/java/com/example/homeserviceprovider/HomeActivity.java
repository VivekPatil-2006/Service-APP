package com.example.homeserviceprovider;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeActivity extends AppCompatActivity {

    private AdView adView;
    private TextView txtWelcome;
    private Button btnLogout,btnMembership;
    private SharedPreferences sharedPreferences;
    private FirebaseFirestore db;

    private static final String PREF_NAME = "ServiceProviderPrefs";
    private static final String KEY_PHONE = "phone";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        btnMembership=findViewById(R.id.btnmembership);

        btnMembership.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),MembershipActivity.class);
                startActivity(intent);
            }
        });

        // Initialize Mobile Ads
        MobileAds.initialize(this, initializationStatus -> { });

        // Load Ad
        adView = findViewById(R.id.adView);  // Fixed: Removed 'view'
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String phone = sharedPreferences.getString(KEY_PHONE, ""); // Retrieve stored phone number

        // Initialize UI elements
        txtWelcome = findViewById(R.id.txtWelcome);
        btnLogout = findViewById(R.id.btnLogout);
        Button btnBookings = findViewById(R.id.btnBookings);
        Button btnProfile = findViewById(R.id.btnProfile);

        // Display phone number if available
        if (phone != null && !phone.isEmpty()) {
            txtWelcome.setText("Welcome, Service Provider: " + phone);
        } else {
            txtWelcome.setText("Welcome, Service Provider");
            Toast.makeText(this, "Phone number not found. Please re-login.", Toast.LENGTH_SHORT).show();
        }

        // Bookings Button Click
        btnBookings.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, BookingsActivity.class);
            startActivity(intent);
        });

        // Profile Button Click
        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        // Logout Button Click
        btnLogout.setOnClickListener(v -> logoutUser(phone));
    }

    // Logout function with Firestore user deletion
    private void logoutUser(String phone) {
        if (phone != null && !phone.isEmpty()) {
            db.collection("service_providers").document(phone)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(HomeActivity.this, "User deleted successfully", Toast.LENGTH_SHORT).show();

                        // Clear SharedPreferences
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.apply();

                        // Redirect to AuthActivity
                        Intent intent = new Intent(HomeActivity.this, AuthActivity.class);
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(HomeActivity.this, "Failed to delete user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(HomeActivity.this, "Invalid phone number. Please re-login.", Toast.LENGTH_SHORT).show();
        }
    }
}
