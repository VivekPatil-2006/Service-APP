package com.example.homeserviceadmin;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button btnViewServiceProviders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the button
        btnViewServiceProviders = findViewById(R.id.button);

        // Set click listener
        btnViewServiceProviders.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AdminServiceProviderActivity.class);
            startActivity(intent);
        });
    }
}
