package com.example.homeserviceprovider;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;

public class NotificationsActivity extends AppCompatActivity {

    private TextView tvNotifications;
    private FirebaseFirestore db;
    private SharedPreferences sharedPreferences;

    private static final String PREF_NAME = "ServiceProviderPrefs";
    private static final String KEY_PHONE = "phone";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        /*
        // Initialize Firebase
        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String phone = sharedPreferences.getString(KEY_PHONE, "");

        // Initialize UI element
        tvNotifications = findViewById(R.id.tvNotifications);

        // Load Notifications
        loadNotifications(phone);
    }

    // Load notifications from Firestore
    private void loadNotifications(String phone) {
        CollectionReference messagesRef = db.collection("notifications").document(phone).collection("messages");

        messagesRef.get().addOnCompleteListener(new OnCompleteListener<com.google.firebase.firestore.QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<com.google.firebase.firestore.QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    StringBuilder allMessages = new StringBuilder();
                    List<DocumentSnapshot> docs = task.getResult().getDocuments();

                    if (docs.isEmpty()) {
                        tvNotifications.setText("No notifications yet.");
                        return;
                    }

                    for (DocumentSnapshot document : docs) {
                        String message = document.getString("message");
                        allMessages.append("• ").append(message).append("\n\n");
                    }

                    tvNotifications.setText(allMessages.toString());
                } else {
                    Toast.makeText(NotificationsActivity.this, "Failed to load notifications!", Toast.LENGTH_SHORT).show();
                }
            }
        });

         */
    }
}
