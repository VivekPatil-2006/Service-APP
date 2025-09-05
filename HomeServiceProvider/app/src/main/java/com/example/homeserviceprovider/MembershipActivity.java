package com.example.homeserviceprovider;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class MembershipActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private String phoneNumber = "9529592099"; // Replace with actual user phone number
    private String upiId = "9529592099@fam"; // UPI ID (Change if needed)
    private Button btnPay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_membership);

        btnPay = findViewById(R.id.btnPay);
        db = FirebaseFirestore.getInstance();

        // Check payment status
        checkPaymentStatus();

        btnPay.setOnClickListener(v -> startUPIPayment());
    }

    private void checkPaymentStatus() {
        DocumentReference providerRef = db.collection("service_providers").document(phoneNumber);
        providerRef.addSnapshotListener((snapshot, error) -> {
            if (error != null) {
                return;
            }
            if (snapshot != null && snapshot.exists()) {
                String paymentStatus = snapshot.getString("payment");
                if ("done".equals(paymentStatus)) {
                    btnPay.setText("Membership Active");
                    btnPay.setEnabled(false);
                }
            }
        });
    }

    private void startUPIPayment() {
        Uri uri = Uri.parse("upi://pay")
                .buildUpon()
                .appendQueryParameter("pa", upiId) // UPI ID
                .appendQueryParameter("pn", "Vivek Patil") // Payee Name
                .appendQueryParameter("tn", "Monthly Membership Payment") // Transaction Note
                .appendQueryParameter("am", "1.00") // Amount
                .appendQueryParameter("mc", "1234") // 🔹 Merchant Code (Fix for Paytm UPI error)
                .appendQueryParameter("cu", "INR") // Currency
                .build();

        Intent upiIntent = new Intent(Intent.ACTION_VIEW);
        upiIntent.setData(uri);

        // Use a chooser to allow users to select any UPI app (Google Pay, Paytm, PhonePe, etc.)
        Intent chooser = Intent.createChooser(upiIntent, "Pay with");

        try {
            startActivityForResult(chooser, 123);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No UPI app found, please install one.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 123) {
            if (data != null) {
                String response = data.getStringExtra("response");
                if (response != null && response.toLowerCase().contains("success")) {
                    updateFirestorePaymentStatus();
                } else {
                    Toast.makeText(this, "Payment Failed", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Transaction Cancelled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateFirestorePaymentStatus() {
        DocumentReference providerRef = db.collection("service_providers").document(phoneNumber);
        providerRef.update("payment", "done")
                .addOnSuccessListener(aVoid -> {
                    btnPay.setText("Membership Active");
                    btnPay.setEnabled(false);
                    Toast.makeText(MembershipActivity.this, "Payment Successful!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(MembershipActivity.this, "Firestore Update Failed", Toast.LENGTH_SHORT).show());
    }
}
