package com.example.homeserviceuser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditActivity extends AppCompatActivity {

    private EditText editName, editLocation, editAge, editPhone;
    private Button btnSave;
    private FirebaseFirestore db;
    private String userPhoneNumber;
    private FusedLocationProviderClient fusedLocationClient;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize views
        editName = findViewById(R.id.editName);
        editLocation = findViewById(R.id.editLocation);
        editAge = findViewById(R.id.editAge);
        editPhone = findViewById(R.id.editPhone);
        btnSave = findViewById(R.id.btnSave);

        // Initialize location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Make phone number non-editable
        editPhone.setEnabled(false);

        // Retrieve phone number from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        userPhoneNumber = sharedPreferences.getString("mobileNumber", null);

        if (userPhoneNumber != null) {
            editPhone.setText(userPhoneNumber);
            fetchCustomerData();
        } else {
            Toast.makeText(this, "Phone number not found in session.", Toast.LENGTH_SHORT).show();
        }

        // Save button functionality
        btnSave.setOnClickListener(view -> updateCustomerDetails());

        // Fetch location on clicking location field
        editLocation.setOnClickListener(v -> fetchCurrentLocation());
    }

    private void fetchCustomerData() {
        DocumentReference customerRef = db.collection("Customer").document(userPhoneNumber);
        customerRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Set fetched data to the edit texts
                    editName.setText(document.getString("name"));
                    editLocation.setText(document.getString("location"));
                    editAge.setText(document.getString("age"));
                    editPhone.setText(document.getString("phone")); // Set phone number
                } else {
                    Toast.makeText(this, "Customer data not found.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Failed to fetch customer data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateCustomerDetails() {
        String name = editName.getText().toString().trim();
        String location = editLocation.getText().toString().trim();
        String age = editAge.getText().toString().trim();

        if (name.isEmpty() || location.isEmpty() || age.isEmpty()) {
            Toast.makeText(this, "Please fill all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> customerDetails = new HashMap<>();
        customerDetails.put("name", name);
        customerDetails.put("location", location);
        customerDetails.put("age", age);

        db.collection("Customer").document(userPhoneNumber)
                .update(customerDetails)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Details updated successfully!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to update details.", Toast.LENGTH_SHORT).show());
    }

    // Fetch user's current location
    private void fetchCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        String currentLocation = "Lat: " + location.getLatitude() + ", Lon: " + location.getLongitude();
                        editLocation.setText(currentLocation);
                    } else {
                        Toast.makeText(EditActivity.this, "Unable to fetch location.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    // Handle permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchCurrentLocation();
            } else {
                Toast.makeText(this, "Location permission denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
