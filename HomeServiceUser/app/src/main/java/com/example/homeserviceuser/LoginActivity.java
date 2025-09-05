package com.example.homeserviceuser;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Random;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextMobile;
    private Button btnGetCode, btnSkip;
    private String generatedOtp;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        // Check if user is already logged in
        boolean isLoggedIn = getSharedPreferences("UserSession", MODE_PRIVATE)
                .getBoolean("isLoggedIn", false);
        if (isLoggedIn) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
            return;
        }

        editTextMobile = findViewById(R.id.editTextMobile);
        btnGetCode = findViewById(R.id.btnGetCode);
        //btnSkip = findViewById(R.id.btnSkip);

        editTextMobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnGetCode.setEnabled(s.length() == 10);
                btnGetCode.setBackgroundTintList(ContextCompat.getColorStateList(
                        LoginActivity.this, s.length() == 10 ? R.color.blue : R.color.gray));
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnGetCode.setOnClickListener(view -> {
            String mobile = "+91" + editTextMobile.getText().toString().trim();
            generatedOtp = generateOtp();
            deleteExistingUserAndSendOtp(mobile, generatedOtp);
        });
        /*
        btnSkip.setOnClickListener(view -> {
            getSharedPreferences("UserSession", MODE_PRIVATE)
                    .edit()
                    .putBoolean("isLoggedIn", true)
                    .apply();

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });*/
    }

    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    private void deleteExistingUserAndSendOtp(String mobile, String otp) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            currentUser.delete().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    sendOtpToUser(mobile, otp);
                } else {
                    Toast.makeText(this, "Failed to delete existing account.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            sendOtpToUser(mobile, otp);
        }
    }

    private void sendOtpToUser(String mobile, String otp) {
        Toast.makeText(this, "OTP sent to " + mobile + ": " + otp, Toast.LENGTH_LONG).show();

        Intent intent = new Intent(LoginActivity.this, OtpVerificationActivity.class);
        intent.putExtra("generatedOtp", otp);
        intent.putExtra("mobile", mobile);
        startActivity(intent);
    }
}
