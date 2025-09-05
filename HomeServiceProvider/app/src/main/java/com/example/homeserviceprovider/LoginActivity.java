package com.example.homeserviceprovider;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Random;

public class LoginActivity extends AppCompatActivity {

    private EditText etPhone, etOTP;
    private Button btnSendOTP, btnVerifyOTP;
    private String generatedOTP;
    private FirebaseFirestore db;
    private SharedPreferences sharedPreferences;

    private static final String PREF_NAME = "ServiceProviderPrefs";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Firestore
        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        // Check if user is already logged in
        if (sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)) {
            navigateToHomeScreen();
        }

        // Initialize UI elements
        etPhone = findViewById(R.id.etPhone);
        etOTP = findViewById(R.id.etOTP);
        btnSendOTP = findViewById(R.id.btnSendOTP);
        btnVerifyOTP = findViewById(R.id.btnVerifyOTP);

        // Send OTP after validating phone number
        btnSendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validatePhoneNumberAndSendOTP();
            }
        });

        // Verify OTP
        btnVerifyOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyOTP();
            }
        });
    }

    // Step 1: Validate phone number from Firestore before sending OTP
    private void validatePhoneNumberAndSendOTP() {
        final String phone = etPhone.getText().toString().trim();

        if (TextUtils.isEmpty(phone) || phone.length() < 10) {
            Toast.makeText(this, "Enter a valid phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if phone number exists in Firestore
        db.collection("service_providers").document(phone).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful() && task.getResult().exists()) {
                            // Phone number found → Generate and send OTP
                            generateAndSendOTP();
                        } else {
                            // Phone number not found
                            Toast.makeText(LoginActivity.this, "Phone number not registered!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // Step 2: Generate and Send OTP
    private void generateAndSendOTP() {
        Random random = new Random();
        generatedOTP = String.format("%06d", random.nextInt(1000000));

        // Simulate sending OTP (Show in Toast for now)
        Toast.makeText(this, "OTP Sent: " + generatedOTP, Toast.LENGTH_LONG).show();

        // In real-world scenarios, integrate an SMS API here
    }

    // Step 3: Verify OTP
    private void verifyOTP() {
        String enteredOTP = etOTP.getText().toString().trim();

        if (TextUtils.isEmpty(enteredOTP)) {
            Toast.makeText(this, "Enter OTP", Toast.LENGTH_SHORT).show();
            return;
        }

        // Compare entered OTP with generated OTP
        if (enteredOTP.equals(generatedOTP)) {
            // Save login state in SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(KEY_IS_LOGGED_IN, true);
            editor.putString(KEY_PHONE, etPhone.getText().toString().trim());
            editor.apply();

            Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show();
            navigateToHomeScreen();
        } else {
            Toast.makeText(this, "Incorrect OTP!", Toast.LENGTH_SHORT).show();
        }
    }

    // Navigate to Home Screen
    private void navigateToHomeScreen() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        intent.putExtra("phone", sharedPreferences.getString(KEY_PHONE, ""));
        startActivity(intent);
        finish();
    }
}
