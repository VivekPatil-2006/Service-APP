package com.example.homeserviceadmin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.firestore.FirebaseFirestore;

public class AuthenticationActivity extends AppCompatActivity {

    private EditText editTextPassword;
    private AdView adView;
    private Button btnAuthenticate;
    private String providerPhone;
    private final String ADMIN_PASSWORD = "989898";
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        // Initialize Mobile Ads
        MobileAds.initialize(this, initializationStatus -> {});

        adView = findViewById(R.id.adView);  // Fix: Use the class variable
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        db = FirebaseFirestore.getInstance();
        providerPhone = getIntent().getStringExtra("phone");

        editTextPassword = findViewById(R.id.editTextPassword);
        btnAuthenticate = findViewById(R.id.btnAuthenticate);

        btnAuthenticate.setOnClickListener(view -> {
            String enteredPassword = editTextPassword.getText().toString().trim();
            if (enteredPassword.equals(ADMIN_PASSWORD)) {
                deleteServiceProvider();
            } else {
                Toast.makeText(this, "Incorrect Password!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteServiceProvider() {
        db.collection("service_providers").document(providerPhone)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Service provider deleted successfully.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AuthenticationActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to delete service provider.", Toast.LENGTH_SHORT).show());
    }
}
