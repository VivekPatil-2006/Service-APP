package com.example.homeserviceprovider;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;

public class SplashScreenActivity extends AppCompatActivity {

    private static final String PREF_NAME = "ServiceProviderPrefs";
    private static final String KEY_PHONE = "phone";
    private static final int SPLASH_TIME = 2000; // 2 seconds delay

    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if user is already registered
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String phone = sharedPreferences.getString(KEY_PHONE, null);

        // Delay before moving to the next screen
        handler.postDelayed(() -> {
            if (phone != null) {
                // User is already registered, go to HomeActivity
                startActivity(new Intent(SplashScreenActivity.this, HomeActivity.class));
            } else {
                // User is not registered, go to AuthActivity
                startActivity(new Intent(SplashScreenActivity.this, AuthActivity.class));
            }
            finish(); // Close Splash Screen
        }, SPLASH_TIME);
    }
}
