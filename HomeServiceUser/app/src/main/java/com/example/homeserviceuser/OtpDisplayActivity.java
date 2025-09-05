package com.example.homeserviceuser;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class OtpDisplayActivity extends AppCompatActivity {

    private TextView tvOtp;
    private FirebaseFirestore firestore;
    private String userPhoneNumber;
    private static final String TAG = "OtpDisplayActivity"; // Log tag for debugging

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_display);

        tvOtp = findViewById(R.id.tvOtp);
        firestore = FirebaseFirestore.getInstance();

        // Retrieve phone number from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        userPhoneNumber = sharedPreferences.getString("mobileNumber", null);

        // Debugging log
        Log.d(TAG, "Retrieved phone number: " + userPhoneNumber);

        // Check if userPhoneNumber is valid
        if (userPhoneNumber == null || userPhoneNumber.length() < 10) {
            Toast.makeText(this, "Invalid phone number", Toast.LENGTH_SHORT).show();
            tvOtp.setText("Invalid phone number");
            return;
        }

        // Extract last 10 digits (Ensure format consistency)
        userPhoneNumber = userPhoneNumber.substring(userPhoneNumber.length() - 10);
        Log.d(TAG, "Formatted phone number: " + userPhoneNumber);

        // Show a toast for debugging
        Toast.makeText(this, "Fetching OTP for: " + userPhoneNumber, Toast.LENGTH_SHORT).show();

        // Fetch OTP
        fetchOtp();
    }

    private void fetchOtp() {
        firestore.collection("otp_storage").document(userPhoneNumber)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Log.d(TAG, "Firestore response received");

                    if (documentSnapshot.exists()) {
                        Log.d(TAG, "Document exists: " + documentSnapshot.getData());

                        // Convert OTP to String safely
                        Object otpObject = documentSnapshot.get("otp");
                        if (otpObject != null) {
                            String otp = String.valueOf(otpObject);  // Convert to String
                            tvOtp.setText("Your OTP: " + otp);
                        } else {
                            tvOtp.setText("OTP not found.");
                            Log.d(TAG, "OTP field missing in document");
                        }
                    } else {
                        tvOtp.setText("No OTP found for this number.");
                        Log.d(TAG, "Document does not exist in Firestore.");
                    }
                })
                .addOnFailureListener(e -> {
                    tvOtp.setText("Failed to fetch OTP.");
                    Toast.makeText(this, "Failed to fetch OTP: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error fetching OTP: ", e);
                });
    }

}
