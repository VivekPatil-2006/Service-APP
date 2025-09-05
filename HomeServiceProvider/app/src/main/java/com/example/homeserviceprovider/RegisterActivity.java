package com.example.homeserviceprovider;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText etName, etPhone, etAge, etService, etLocation;
    private Button btnRegister;
    private FirebaseFirestore db;
    private SharedPreferences sharedPreferences;

    private static final String PREF_NAME = "ServiceProviderPrefs";
    private static final String KEY_PHONE = "phone";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase & SharedPreferences
        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();
        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        // Initialize UI elements
        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etAge = findViewById(R.id.etAge);
        etService = findViewById(R.id.etService);
        etLocation = findViewById(R.id.etLocation);
        btnRegister = findViewById(R.id.btnRegister);

        // Handle Registration Button Click
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerServiceProvider();
            }
        });
    }

    private void registerServiceProvider() {
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String age = etAge.getText().toString().trim();
        String service = etService.getText().toString().trim();
        String location = etLocation.getText().toString().trim();

        // Validation
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(age) ||
                TextUtils.isEmpty(service) || TextUtils.isEmpty(location)) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate phone number (must be 10 digits)
        if (!Patterns.PHONE.matcher(phone).matches() || phone.length() != 10) {
            Toast.makeText(this, "Enter a valid 10-digit phone number!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a new Service Provider object
        Map<String, Object> serviceProvider = new HashMap<>();
        serviceProvider.put("name", name);
        serviceProvider.put("phone", phone);
        serviceProvider.put("age", age);
        serviceProvider.put("service", service);
        serviceProvider.put("location", location);

        // Store data in Firestore
        db.collection("service_providers")
                .document(phone) // Using phone number as unique ID
                .set(serviceProvider)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Save registration status
                            sharedPreferences.edit().putString(KEY_PHONE, phone).apply();

                            Toast.makeText(RegisterActivity.this, "Registration Successful!", Toast.LENGTH_SHORT).show();

                            // Redirect to HomeActivity
                            startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Failed to Register!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
