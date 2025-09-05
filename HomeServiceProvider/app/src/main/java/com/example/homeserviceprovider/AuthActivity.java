package com.example.homeserviceprovider;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class AuthActivity extends AppCompatActivity {

    private TextInputLayout layoutName, layoutAge, layoutService, layoutLocation, layoutPhone, layoutOTP;
    private TextInputEditText editTextLocation;
    private MaterialButton btnSendOTP, btnAuthenticate;
    private FirebaseFirestore db;
    private SharedPreferences sharedPreferences;
    private String generatedOTP;
    private boolean isUserRegistered = false;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private FusedLocationProviderClient fusedLocationClient;

    private static final String PREF_NAME = "ServiceProviderPrefs";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();
        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Initialize Components
        layoutPhone = findViewById(R.id.layoutPhone);
        layoutOTP = findViewById(R.id.layoutOTP);
        layoutName = findViewById(R.id.layoutName);
        layoutAge = findViewById(R.id.layoutAge);
        layoutService = findViewById(R.id.layoutService);
        layoutLocation = findViewById(R.id.layoutLocation);
        editTextLocation = (TextInputEditText) layoutLocation.getEditText();

        btnSendOTP = findViewById(R.id.btnSendOTP);
        btnAuthenticate = findViewById(R.id.btnAuthenticate);

        if (sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)) {
            navigateToHomeScreen();
        }

        btnSendOTP.setOnClickListener(v -> validatePhoneNumberAndSendOTP());
        btnAuthenticate.setOnClickListener(v -> authenticateUser());

        // Fetch location when location field is clicked
        editTextLocation.setOnClickListener(v -> fetchCurrentLocation());
    }

    private void fetchCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            String locationString = "Lat: " + location.getLatitude() + ", Lon: " + location.getLongitude();
                            editTextLocation.setText(locationString);
                        } else {
                            Toast.makeText(AuthActivity.this, "Unable to fetch location", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            fetchCurrentLocation();
        } else {
            Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    private void validatePhoneNumberAndSendOTP() {
        final String phone = layoutPhone.getEditText().getText().toString().trim();
        if (TextUtils.isEmpty(phone) || phone.length() < 10) {
            layoutPhone.setError("Enter a valid phone number");
            return;
        }

        db.collection("service_providers").document(phone).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                isUserRegistered = true;
                hideRegistrationFields();
            } else {
                isUserRegistered = false;
                showRegistrationFields();
            }
            generateAndSendOTP();
        });
    }

    private void generateAndSendOTP() {
        Random random = new Random();
        generatedOTP = String.format("%06d", random.nextInt(1000000));
        Toast.makeText(this, "OTP Sent: " + generatedOTP, Toast.LENGTH_LONG).show();
    }

    private void showRegistrationFields() {
        layoutName.setVisibility(View.VISIBLE);
        layoutAge.setVisibility(View.VISIBLE);
        layoutService.setVisibility(View.VISIBLE);
        layoutLocation.setVisibility(View.VISIBLE);
    }

    private void hideRegistrationFields() {
        layoutName.setVisibility(View.GONE);
        layoutAge.setVisibility(View.GONE);
        layoutService.setVisibility(View.GONE);
        layoutLocation.setVisibility(View.GONE);
    }

    private void authenticateUser() {
        String phone = layoutPhone.getEditText().getText().toString().trim();
        String enteredOTP = layoutOTP.getEditText().getText().toString().trim();

        if (TextUtils.isEmpty(enteredOTP)) {
            layoutOTP.setError("Enter OTP");
            return;
        }

        if (!enteredOTP.equals(generatedOTP)) {
            layoutOTP.setError("Incorrect OTP!");
            return;
        }

        if (!isUserRegistered) {
            registerNewUser(phone);
        } else {
            loginUser(phone);
        }
    }

    private void registerNewUser(String phone) {
        String name = layoutName.getEditText().getText().toString().trim();
        String age = layoutAge.getEditText().getText().toString().trim();
        String service = layoutService.getEditText().getText().toString().trim();
        String location = layoutLocation.getEditText().getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(age) || TextUtils.isEmpty(service) || TextUtils.isEmpty(location)) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> serviceProvider = new HashMap<>();
        serviceProvider.put("name", name);
        serviceProvider.put("phone", phone);
        serviceProvider.put("age", age);
        serviceProvider.put("service", service);
        serviceProvider.put("location", location);

        db.collection("service_providers").document(phone).set(serviceProvider)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        loginUser(phone);
                    } else {
                        Toast.makeText(AuthActivity.this, "Registration Failed!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loginUser(String phone) {
        sharedPreferences.edit().putBoolean(KEY_IS_LOGGED_IN, true).putString(KEY_PHONE, phone).apply();
        Toast.makeText(AuthActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
        navigateToHomeScreen();
    }

    private void navigateToHomeScreen() {
        startActivity(new Intent(AuthActivity.this, HomeActivity.class));
        finish();
    }
}
