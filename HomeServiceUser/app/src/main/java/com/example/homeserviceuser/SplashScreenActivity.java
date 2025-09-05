package com.example.homeserviceuser;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            // User is logged in
            startActivity(new Intent(this, MainActivity.class));
        } else {
            // User is not logged in
            startActivity(new Intent(this, LoginActivity.class));
        }

        finish(); // Close SplashScreenActivity
    }
}
