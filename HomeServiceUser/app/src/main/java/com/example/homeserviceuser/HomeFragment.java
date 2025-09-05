package com.example.homeserviceuser;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.card.MaterialCardView;

public class HomeFragment extends Fragment {
    private AdView adView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize Mobile Ads
        MobileAds.initialize(getContext(), initializationStatus -> { });

       // Load Ad
        adView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        // Load Ad 2
        adView = view.findViewById(R.id.adView2);
        AdRequest adRequest2 = new AdRequest.Builder().build();
        adView.loadAd(adRequest2);

        // Initialize all card views and set onClickListeners with corresponding service types
        setCardClickListener(view, R.id.salonService, "Salon Service");
        setCardClickListener(view, R.id.handymanService, "Plumbing Services");
        setCardClickListener(view, R.id.applianceRepair, "Appliance Repair");
        setCardClickListener(view, R.id.cleaningService, "Cleaning & Pest Control");
        setCardClickListener(view, R.id.mosquitoNetService, "Safety Service");
        setCardClickListener(view, R.id.moversService, "Movers & Storage");
        setCardClickListener(view, R.id.fitnessService, "Renovation & Fabrication");
        setCardClickListener(view, R.id.homeTutorService, "Painting & Wall Maker");

        return view;
    }

    private void setCardClickListener(View view, int cardId, String serviceType) {
        MaterialCardView card = view.findViewById(cardId);
        if (card != null) {
            card.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), ServiceProviderListActivity.class);
                intent.putExtra("service_type", serviceType);
                startActivity(intent);
            });
        }
    }
/*
    @Override
    public void onResume() {
        super.onResume();
        if (adView != null) {
            adView.resume();
        }
    }

    @Override
    public void onPause() {
        if (adView != null) {
            adView.pause();
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }

 */
}
