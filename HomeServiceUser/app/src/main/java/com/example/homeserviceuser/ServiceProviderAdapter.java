package com.example.homeserviceuser;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ServiceProviderAdapter extends RecyclerView.Adapter<ServiceProviderAdapter.ViewHolder> {

    private List<ServiceProvider> serviceProviderList;
    private Context context;

    public ServiceProviderAdapter(Context context, List<ServiceProvider> serviceProviderList) {
        this.context = context;
        this.serviceProviderList = serviceProviderList;
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
        holder.name.setText(provider.getName());
        holder.service.setText("Service: " + provider.getService());
        holder.location.setText("Location: " + provider.getLocation());
        holder.phone.setText("Phone: " + provider.getPhone());
        holder.age.setText("Age: " + provider.getAge());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, BookingActivity.class);
            intent.putExtra("provider_name", provider.getName());
            intent.putExtra("provider_phone", provider.getPhone());
            intent.putExtra("provider_service", provider.getService());
            intent.putExtra("provider_location", provider.getLocation());
            intent.putExtra("provider_age", provider.getAge());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return serviceProviderList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, service, location, phone, age;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.providerName);
            service = itemView.findViewById(R.id.providerService);
            location = itemView.findViewById(R.id.providerLocation);
            phone = itemView.findViewById(R.id.providerPhone);
            age = itemView.findViewById(R.id.providerAge);
        }
    }
}
