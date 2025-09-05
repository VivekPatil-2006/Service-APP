package com.example.homeserviceuser;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class AccountFragment extends Fragment {
    private AdView adView;

    private Button btnLogin, btnEdit, btnShare, btnRate, btnAbout, btnTerms, btnPrivacy, btnLogout;
    private ImageButton Notification;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        // Initialize Mobile Ads
        MobileAds.initialize(getContext(), initializationStatus -> { });

        // Load Ad
        adView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        // Initialize buttons
        btnLogin = view.findViewById(R.id.btn_login);
        btnEdit = view.findViewById(R.id.btn_edit);
        btnShare = view.findViewById(R.id.btn_share);
        btnRate = view.findViewById(R.id.btn_rate);
        btnAbout = view.findViewById(R.id.btn_about);
        btnTerms = view.findViewById(R.id.btn_terms);
        btnPrivacy = view.findViewById(R.id.btn_privacy);
        btnLogout = view.findViewById(R.id.btnLogout);
        Notification=view.findViewById(R.id.btn_notification);

        Notification.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), OtpDisplayActivity.class);
            startActivity(intent);
        });

        // 1. Login Button -> Redirect to LoginActivity
        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            Toast.makeText(getActivity(), "Already Account is Created", Toast.LENGTH_SHORT).show();
            startActivity(intent);
        });
        // Edit Profile
        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditActivity.class);
            startActivity(intent);
        });


        // 2. Share Button -> Share only the app name
        btnShare.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            String shareMessage = "Check out Home Service App!";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "Share via"));
        });

        // 3. Rate the App -> Open Play Store
        btnRate.setOnClickListener(v -> {
            try {
                // Open the app page in Play Store
                Uri uri = Uri.parse("market://details?id=" + requireActivity().getPackageName());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                // If Play Store app is not installed, open Play Store in browser
                Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=" + requireActivity().getPackageName());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        // 4. About App -> Redirect to AboutApp Activity
        btnAbout.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AboutApp.class);
            startActivity(intent);
        });

        // 5. Terms and Conditions -> Redirect to TermsConditions Activity
        btnTerms.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), TermsConditions.class);
            startActivity(intent);
        });

        // 6. Privacy Policy -> Redirect to PrivacyPolicy Activity
        btnPrivacy.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PrivacyPolicy.class);
            startActivity(intent);
        });

        // 7. Logout button is already implemented.
        btnLogout.setOnClickListener(v -> {
            Toast.makeText(getContext(), "App is in Testing Mode", Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}
