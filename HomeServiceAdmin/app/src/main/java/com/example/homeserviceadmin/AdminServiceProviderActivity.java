package com.example.homeserviceadmin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;

public class AdminServiceProviderActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdView adView;

    private ServiceProviderAdapter adapter;
    private List<ServiceProvider> serviceProviderList;
    private FirebaseFirestore db;
    private ProgressDialog progressDialog; // Loading bar

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_service_provider);

        // Initialize Mobile Ads
        MobileAds.initialize(this, initializationStatus -> {});

        adView = findViewById(R.id.adView);  // Fix: Use the class variable
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        recyclerView = findViewById(R.id.recyclerViewServiceProviders);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        serviceProviderList = new ArrayList<>();

        // Adapter with click listener
        adapter = new ServiceProviderAdapter(this, serviceProviderList, provider -> {
            Intent intent = new Intent(AdminServiceProviderActivity.this, ProviderActivity.class);
            intent.putExtra("phone", provider.getPhone()); // Passing phone number
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize and show loading bar
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching Service Providers...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        fetchServiceProviders();
    }

    private void fetchServiceProviders() {
        db.collection("service_providers")
                .get()
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss(); // Hide loading bar after fetching
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            serviceProviderList.clear();
                            for (DocumentSnapshot document : querySnapshot) {
                                ServiceProvider provider = document.toObject(ServiceProvider.class);
                                serviceProviderList.add(provider);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(this, "Failed to fetch service providers.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
