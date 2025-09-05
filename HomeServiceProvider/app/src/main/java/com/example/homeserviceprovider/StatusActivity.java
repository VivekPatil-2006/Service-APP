package com.example.homeserviceprovider;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StatusActivity extends AppCompatActivity {

    private TextView txtUser, txtService, txtLocation, txtStatus;
    private EditText otpText;
    private Button btnDone, btnCancel, btnSendOtp;
    private FirebaseFirestore db;
    private String user, service, location, status, phone, phone_no;

    private static final String PREF_NAME = "ServiceProviderPrefs";
    private static final String KEY_PHONE = "phone";
    private static final String OTP_PREF = "OtpPref"; // SharedPreferences for OTP
    private static final String OTP_KEY = "otp";

    private int otp; // OTP variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        db = FirebaseFirestore.getInstance();

        txtUser = findViewById(R.id.txtUser);
        txtService = findViewById(R.id.txtService);
        txtLocation = findViewById(R.id.txtLocation);
        txtStatus = findViewById(R.id.txtStatus);
        otpText = findViewById(R.id.otptext);
        btnSendOtp = findViewById(R.id.btnsendOtp);
        btnDone = findViewById(R.id.btnDone);
        btnCancel = findViewById(R.id.btnCancel);

        // Retrieve phone number from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        phone = sharedPreferences.getString(KEY_PHONE, ""); // Get stored phone number

        if (phone.isEmpty()) {
            Toast.makeText(this, "Phone number not found. Please re-login.", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity
            return;
        }

        // Get data from intent
        user = getIntent().getStringExtra("user");
        service = getIntent().getStringExtra("service");
        location = getIntent().getStringExtra("location");
        status = getIntent().getStringExtra("status");

        // Extract phone number from user string
        Pattern pattern = Pattern.compile("\\b\\d{10}\\b");
        Matcher matcher = pattern.matcher(user);
        if (matcher.find()) {
            phone_no = matcher.group();  // Extract matched phone number
        } else {
            phone_no = ""; // Set empty if no phone number found
        }

        txtUser.setText("User: " + user);
        txtService.setText("Service: " + service);
        txtLocation.setText("Location: " + location);
        txtStatus.setText("Status: " + status);

        btnSendOtp.setOnClickListener(v -> sendOtp());
        btnDone.setOnClickListener(v -> verifyOtpAndCompleteBooking());
        btnCancel.setOnClickListener(v -> deleteBooking());
    }

    // Generate OTP, store it in Firestore & SharedPreferences
    private void sendOtp() {
        if (phone_no.isEmpty()) {
            Toast.makeText(this, "User phone number not found!", Toast.LENGTH_SHORT).show();
            return;
        }

        otp = new Random().nextInt(900000) + 100000; // Generate 6-digit OTP

        // Store OTP in Firestore
        Map<String, Object> otpData = new HashMap<>();
        otpData.put("otp", otp);

        db.collection("otp_storage").document(phone_no)
                .set(otpData)
                .addOnSuccessListener(aVoid -> {
                    saveOtpLocally(otp); // Store OTP in SharedPreferences
                    Toast.makeText(this, "OTP Sent & Stored", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to store OTP", Toast.LENGTH_SHORT).show());
    }

    // Store OTP locally in SharedPreferences
    private void saveOtpLocally(int otp) {
        SharedPreferences.Editor editor = getSharedPreferences(OTP_PREF, MODE_PRIVATE).edit();
        editor.putInt(OTP_KEY, otp);
        editor.apply();
    }

    // Retrieve OTP from SharedPreferences
    private int getStoredOtp() {
        SharedPreferences sharedPreferences = getSharedPreferences(OTP_PREF, MODE_PRIVATE);
        return sharedPreferences.getInt(OTP_KEY, -1); // Return -1 if no OTP stored
    }

    private void verifyOtpAndCompleteBooking() {
        String enteredOtp = otpText.getText().toString().trim();

        if (enteredOtp.isEmpty()) {
            Toast.makeText(this, "Please enter OTP", Toast.LENGTH_SHORT).show();
            return;
        }

        int storedOtp = getStoredOtp(); // Retrieve stored OTP

        if (enteredOtp.equals(String.valueOf(storedOtp))) {
            updateStatus("complete");
            Toast.makeText(this, "Verified Successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Entered OTP is not valid", Toast.LENGTH_SHORT).show();
        }
    }

    // Update booking status in Firestore
    private void updateStatus(String newStatus) {
        db.collection("bookings").document(phone).collection("requests")
                .whereEqualTo("service", service)
                .whereEqualTo("location", location)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        document.getReference().update("status", newStatus)
                                .addOnSuccessListener(aVoid -> {
                                    txtStatus.setText("Status: " + newStatus);
                                    Toast.makeText(this, "Status Updated", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> Toast.makeText(this, "Failed to update status", Toast.LENGTH_SHORT).show());
                    }
                });
    }

    // Delete booking from Firestore
    private void deleteBooking() {
        db.collection("bookings").document(phone).collection("requests")
                .whereEqualTo("service", service)
                .whereEqualTo("location", location)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        document.getReference().delete()
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, "Booking Cancelled", Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .addOnFailureListener(e -> Toast.makeText(this, "Failed to delete booking", Toast.LENGTH_SHORT).show());
                    }
                });
    }
}
