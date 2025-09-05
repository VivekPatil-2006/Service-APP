package com.example.homeserviceadmin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ServiceProviderAdapter extends RecyclerView.Adapter<ServiceProviderAdapter.ViewHolder> {

    private Context context;
    private List<ServiceProvider> serviceProviderList;
    private OnItemClickListener listener;

    // Interface for handling click events
    public interface OnItemClickListener {
        void onItemClick(ServiceProvider provider);
    }

    // Updated constructor
    public ServiceProviderAdapter(Context context, List<ServiceProvider> serviceProviderList, OnItemClickListener listener) {
        this.context = context;
        this.serviceProviderList = serviceProviderList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_service_provider, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ServiceProvider provider = serviceProviderList.get(position);
        holder.name.setText("Name: " + provider.getName());
        holder.phone.setText("Phone: " + provider.getPhone());
        holder.age.setText("Age: " + provider.getAge());
        holder.location.setText("Location: " + provider.getLocation());
        holder.service.setText("Service: " + provider.getService());

        // Handle item click
        holder.itemView.setOnClickListener(view -> listener.onItemClick(provider));
    }

    @Override
    public int getItemCount() {
        return serviceProviderList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, phone, age, location, service;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvName);
            phone = itemView.findViewById(R.id.tvPhone);
            age = itemView.findViewById(R.id.tvAge);
            location = itemView.findViewById(R.id.tvLocation);
            service = itemView.findViewById(R.id.tvService);
        }
    }
}
