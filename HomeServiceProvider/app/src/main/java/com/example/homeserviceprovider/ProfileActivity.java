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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private EditText etName, etPhone, etAge, etService, etLocation;
    private Button btnUpdateProfile;
    private FirebaseFirestore db;
    private SharedPreferences sharedPreferences;

    private static final String PREF_NAME = "ServiceProviderPrefs";
    private static final String KEY_PHONE = "phone";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Firebase Firestore
        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String phone = sharedPreferences.getString(KEY_PHONE, "");

        // Initialize UI elements
        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etAge = findViewById(R.id.etAge);
        etService = findViewById(R.id.etService);
        etLocation = findViewById(R.id.etLocation);
        btnUpdateProfile = findViewById(R.id.btnUpdateProfile);

        // Initialize location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Set phone number as read-only
        etPhone.setText(phone);
        etPhone.setEnabled(false);

        // Load profile details
        loadProfile(phone);

        // Fetch location on clicking location field
        etLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchCurrentLocation();
            }
        });

        // Update profile button click
        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile(phone);
            }
        });
    }

    // Step 1: Load profile details from Firestore
    private void loadProfile(String phone) {
        db.collection("service_providers").document(phone)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful() && task.getResult().exists()) {
                            DocumentSnapshot document = task.getResult();
                            etName.setText(document.getString("name"));
                            etAge.setText(document.getString("age"));
                            etService.setText(document.getString("service"));
                            etLocation.setText(document.getString("location"));
                        } else {
                            Toast.makeText(ProfileActivity.this, "Profile not found!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // Step 2: Update profile details in Firestore
    private void updateProfile(String phone) {
        String name = etName.getText().toString().trim();
        String age = etAge.getText().toString().trim();
        String service = etService.getText().toString().trim();
        String location = etLocation.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(age) || TextUtils.isEmpty(service) || TextUtils.isEmpty(location)) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> profileUpdates = new HashMap<>();
        profileUpdates.put("name", name);
        profileUpdates.put("age", age);
        profileUpdates.put("service", service);
        profileUpdates.put("location", location);

        db.collection("service_providers").document(phone)
                .update(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ProfileActivity.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ProfileActivity.this, "Update failed!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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
                        etLocation.setText(currentLocation);
                    } else {
                        Toast.makeText(ProfileActivity.this, "Unable to fetch location.", Toast.LENGTH_SHORT).show();
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


/*
package com.example.homeserviceprovider;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfileActivity extends AppCompatActivity {

    private EditText etName, etPhone, etAge, etService, etLocation, etAadhar;
    private Button btnUpdateProfile, btnUploadAadhar;
    private ImageView ivAadharImage;
    private FirebaseFirestore db;
    private StorageReference storageReference;
    private SharedPreferences sharedPreferences;

    private static final String PREF_NAME = "ServiceProviderPrefs";
    private static final String KEY_PHONE = "phone";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final int CAMERA_REQUEST_CODE = 1002;
    private static final int GALLERY_REQUEST_CODE = 1003;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String phone = sharedPreferences.getString(KEY_PHONE, "");

        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etAge = findViewById(R.id.etAge);
        etService = findViewById(R.id.etService);
        etLocation = findViewById(R.id.etLocation);
        etAadhar = findViewById(R.id.etAadharNumber);
        ivAadharImage = findViewById(R.id.ivAadharPreview);
        btnUpdateProfile = findViewById(R.id.btnUpdateProfile);
        btnUploadAadhar = findViewById(R.id.btnUploadAadhar);

        etPhone.setText(phone);
        etPhone.setEnabled(false);

        loadProfile(phone);

        btnUploadAadhar.setOnClickListener(v -> selectImage());

        btnUpdateProfile.setOnClickListener(v -> updateProfile(phone));
    }

    private void selectImage() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == GALLERY_REQUEST_CODE && data != null) {
                imageUri = data.getData();
                ivAadharImage.setImageURI(imageUri);
            }
        }
    }

    private void uploadAadharImage(String phone, String aadharNumber) {
        if (imageUri != null) {
            StorageReference fileRef = storageReference.child("aadhar_images/" + phone + ".jpg");
            fileRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    saveAadharDetails(phone, aadharNumber, uri.toString());
                });
            }).addOnFailureListener(e ->
                    Toast.makeText(ProfileActivity.this, "Failed to upload Aadhar image", Toast.LENGTH_SHORT).show()
            );
        }
    }

    private void saveAadharDetails(String phone, String aadharNumber, String imageUrl) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("aadhar_number", aadharNumber);
        updates.put("aadhar_image_url", imageUrl);

        db.collection("service_providers").document(phone).update(updates)
                .addOnSuccessListener(aVoid -> Toast.makeText(ProfileActivity.this, "Aadhar details saved", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(ProfileActivity.this, "Failed to save Aadhar details", Toast.LENGTH_SHORT).show());
    }

    private void updateProfile(String phone) {
        String name = etName.getText().toString().trim();
        String age = etAge.getText().toString().trim();
        String service = etService.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String aadharNumber = etAadhar.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(age) || TextUtils.isEmpty(service) || TextUtils.isEmpty(location) || TextUtils.isEmpty(aadharNumber)) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUri != null) {
            uploadAadharImage(phone, aadharNumber);
        } else {
            saveAadharDetails(phone, aadharNumber, "");
        }
    }

    private void loadProfile(String phone) {
        db.collection("service_providers").document(phone).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                DocumentSnapshot document = task.getResult();
                etName.setText(document.getString("name"));
                etAge.setText(document.getString("age"));
                etService.setText(document.getString("service"));
                etLocation.setText(document.getString("location"));
                etAadhar.setText(document.getString("aadhar_number"));
                String imageUrl = document.getString("aadhar_image_url");
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    ivAadharImage.setImageURI(Uri.parse(imageUrl));
                }
            }
        });
    }
}

 */