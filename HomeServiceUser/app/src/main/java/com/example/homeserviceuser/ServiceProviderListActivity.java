package com.example.homeserviceuser;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ServiceProviderListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ServiceProviderAdapter adapter;
    private List<ServiceProvider> serviceProviderList;
    private FirebaseFirestore firestore;
    private TextView tvTitle;
    private ProgressBar progressBar;
    private FusedLocationProviderClient fusedLocationClient;
    private Location userLocation;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider_list);

        recyclerView = findViewById(R.id.recyclerView);
        tvTitle = findViewById(R.id.tvTitle);
        progressBar = findViewById(R.id.progressBar);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        serviceProviderList = new ArrayList<>();
        adapter = new ServiceProviderAdapter(this, serviceProviderList);
        recyclerView.setAdapter(adapter);

        firestore = FirebaseFirestore.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        String serviceType = getIntent().getStringExtra("service_type");
        if (serviceType != null) {
            tvTitle.setText(serviceType + " Service Providers");
            fetchUserLocation(serviceType.toLowerCase());
        }
    }

    private void fetchUserLocation(String serviceType) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    userLocation = location;
                    fetchServiceProviders(serviceType);
                } else {
                    Toast.makeText(ServiceProviderListActivity.this, "Unable to get location", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void fetchServiceProviders(String serviceType) {
        progressBar.setVisibility(View.VISIBLE);
        CollectionReference serviceProvidersRef = firestore.collection("service_providers");

        serviceProvidersRef.whereEqualTo("service", serviceType)
                .get()
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        serviceProviderList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            ServiceProvider provider = document.toObject(ServiceProvider.class);
                            String locationString = document.getString("location");
                            if (locationString != null) {
                                double[] latLon = extractLatLon(locationString);
                                if (latLon != null && isWithin2Km(latLon[0], latLon[1])) {
                                    serviceProviderList.add(provider);
                                }
                            }
                        }

                        if (serviceProviderList.isEmpty()) {
                            Toast.makeText(this, "No nearby service providers available.", Toast.LENGTH_SHORT).show();
                        }

                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private double[] extractLatLon(String locationString) {
        try {
            String[] parts = locationString.split(",");
            double lat = Double.parseDouble(parts[0].split(":")[1].trim());
            double lon = Double.parseDouble(parts[1].split(":")[1].trim());
            return new double[]{lat, lon};
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean isWithin2Km(double providerLat, double providerLon) {
        if (userLocation == null) return false;
        float[] distance = new float[1];
        Location.distanceBetween(
                userLocation.getLatitude(),
                userLocation.getLongitude(),
                providerLat,
                providerLon,
                distance
        );
        return distance[0] <= 2000; // 2 km in meters
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                String serviceType = getIntent().getStringExtra("service_type");
                fetchUserLocation(serviceType);
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
