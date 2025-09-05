package com.example.homeserviceuser;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class OtpVerificationActivity extends AppCompatActivity {

    private EditText editTextOtp;
    private Button btnVerify;
    private String generatedOtp, mobileNumber;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);

        editTextOtp = findViewById(R.id.editTextOtp);
        btnVerify = findViewById(R.id.btnVerify);

        generatedOtp = getIntent().getStringExtra("generatedOtp");
        mobileNumber = getIntent().getStringExtra("mobile");

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnVerify.setOnClickListener(view -> {
            String otp = editTextOtp.getText().toString().trim();
            if (otp.length() == 6) {
                verifyOtp(otp);
            } else {
                Toast.makeText(OtpVerificationActivity.this, "Enter a valid 6-digit OTP", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void verifyOtp(String enteredOtp) {
        if (enteredOtp.equals(generatedOtp)) {
            // Save user session and phone number in SharedPreferences
            getSharedPreferences("UserSession", MODE_PRIVATE)
                    .edit()
                    .putBoolean("isLoggedIn", true)
                    .putString("mobileNumber", mobileNumber) // Save phone number
                    .apply();

            addCustomerToFirestore();

        } else {
            Toast.makeText(this, "Invalid OTP. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void addCustomerToFirestore() {
        // Creating a basic customer entry
        Map<String, Object> customerData = new HashMap<>();
        customerData.put("phone", mobileNumber);
        customerData.put("name", ""); // Placeholder for name
        customerData.put("location", ""); // Placeholder for location
        customerData.put("age", ""); // Placeholder for age

        // Add or update customer document in Firestore
        db.collection("Customer").document(mobileNumber)
                .set(customerData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Verification Successful! Customer added.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(OtpVerificationActivity.this, MainActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to add customer.", Toast.LENGTH_SHORT).show());
    }
}
