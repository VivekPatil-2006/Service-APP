package com.example.homeserviceadmin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.AdView;

public class ProviderActivity extends AppCompatActivity {

    private Button btnGoToMain, btnDeleteProvider;
    private String providerPhone;
    private AdView adView; // Declared class variable for AdView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider);

        // Initialize Mobile Ads
        MobileAds.initialize(this, initializationStatus -> {});

        // Find AdView and load an ad
        adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        // Getting provider phone from intent
        providerPhone = getIntent().getStringExtra("phone");

        // Initialize buttons
        btnGoToMain = findViewById(R.id.btnGoToMain);
        btnDeleteProvider = findViewById(R.id.btnDeleteProvider);

        // Button Click Listeners
        btnGoToMain.setOnClickListener(view -> {
            Intent intent = new Intent(ProviderActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        btnDeleteProvider.setOnClickListener(view -> {
            // Redirect to AuthenticationActivity for password verification
            Intent intent = new Intent(ProviderActivity.this, AuthenticationActivity.class);
            intent.putExtra("phone", providerPhone); // Pass the phone number
            startActivity(intent);
            finish();
        });
    }
}
