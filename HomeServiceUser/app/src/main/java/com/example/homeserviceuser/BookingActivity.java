package com.example.homeserviceuser;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class BookingActivity extends AppCompatActivity {

    private EditText etLocation, etUser, etStatus;
    private Button btnBook;
    private ProgressBar progressBar;

    private FirebaseFirestore firestore;
    private String providerPhone, service;

    private static final int SMS_PERMISSION_REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        etLocation = findViewById(R.id.etLocation);
        etUser = findViewById(R.id.etUser);
        etStatus = findViewById(R.id.etStatus);
        btnBook = findViewById(R.id.btnBook);
        progressBar = findViewById(R.id.progressBar);

        firestore = FirebaseFirestore.getInstance();

        // Get data from Intent
        providerPhone = getIntent().getStringExtra("provider_phone");
        service = getIntent().getStringExtra("provider_service");

        // Request SMS permission if not granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_REQUEST_CODE);
        }

        btnBook.setOnClickListener(v -> saveBooking());
    }

    private void saveBooking() {
        String location = etLocation.getText().toString().trim();
        String user = etUser.getText().toString().trim();
        String status = etStatus.getText().toString().trim();

        if (location.isEmpty() || user.isEmpty() || status.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        // Save booking details to Firestore
        firestore.collection("bookings").document(providerPhone).collection("requests")
                .get()
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        int nextBookingNumber = task.getResult().size() + 1;
                        String bookingId = "booking" + nextBookingNumber;

                        Map<String, Object> bookingData = new HashMap<>();
                        bookingData.put("location", location);
                        bookingData.put("service", service);
                        bookingData.put("user", user);
                        bookingData.put("status", status);

                        firestore.collection("bookings").document(providerPhone)
                                .collection("requests").document(bookingId)
                                .set(bookingData)
                                .addOnSuccessListener(aVoid -> {
                                    saveBookingToUsers(user, location, service, status);
                                    sendSMS(providerPhone, "I Booked your service");
                                })
                                .addOnFailureListener(e -> Toast.makeText(this, "Failed to book: " + e.getMessage(), Toast.LENGTH_SHORT).show());

                    } else {
                        Toast.makeText(this, "Failed to fetch bookings", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveBookingToUsers(String phone, String location, String service, String status) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("location", location);
        userData.put("name", phone);
        userData.put("service", service);
        userData.put("status", status);

        firestore.collection("users").document(phone)
                .set(userData)
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Booking Successful and saved in Users!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Failed to save booking in Users: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void sendSMS(String phoneNumber, String message) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNumber, null, message, null, null);
                Toast.makeText(this, "Message sent to provider", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "Failed to send message: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "SMS permission not granted", Toast.LENGTH_SHORT).show();
        }
    }
}
