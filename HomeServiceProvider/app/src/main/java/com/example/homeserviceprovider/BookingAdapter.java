package com.example.homeserviceprovider;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    private List<Booking> bookingList;
    private Context context;

    public BookingAdapter(Context context, List<Booking> bookingList) {
        this.context = context;
        this.bookingList = bookingList;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = bookingList.get(position);

        holder.userText.setText("User: " + booking.getUser());
        holder.serviceText.setText("Service: " + booking.getService());
        holder.locationText.setText("Location: " + booking.getLocation());
        holder.statusText.setText("Status: " + booking.getStatus());

        // Handle item click to open StatusActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, StatusActivity.class);
            intent.putExtra("user", booking.getUser());
            intent.putExtra("service", booking.getService());
            intent.putExtra("location", booking.getLocation());
            intent.putExtra("status", booking.getStatus());
            context.startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView userText, serviceText, locationText, statusText;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            userText = itemView.findViewById(R.id.userText);
            serviceText = itemView.findViewById(R.id.serviceText);
            locationText = itemView.findViewById(R.id.locationText);
            statusText = itemView.findViewById(R.id.statusText);
        }
    }
}
