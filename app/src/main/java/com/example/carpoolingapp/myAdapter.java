package com.example.carpoolingapp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

public class myAdapter extends RecyclerView.Adapter<myAdapter.ViewHolder> {

    private final List<Map<String, String>> itemList;
    private final Context mContext;
    private final int rowLayout;
    private OnDriverSelectedListener listener;
    SQLiteDatabase db;

    public myAdapter(List<Map<String, String>> itemList, int rowLayout, Context context) {
        this.itemList = itemList;
        this.mContext = context;
        this.rowLayout = rowLayout;
    }

    public void setOnDriverSelectedListener(OnDriverSelectedListener listener) {
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public RatingBar ratingBar;
        public TextView dateTextView;
        public TextView timeTextView;
        public TextView originTextView;
        public TextView destinationTextView;
        public TextView priceTextView;

        public ViewHolder(View itemView, int rowLayout) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.Name);

            // Initialize fields for item_driver
            if (rowLayout == R.layout.item_driver) {
                dateTextView = itemView.findViewById(R.id.Date);
                timeTextView = itemView.findViewById(R.id.Time);
                originTextView = itemView.findViewById(R.id.origin);
                destinationTextView = itemView.findViewById(R.id.destination);
                priceTextView = itemView.findViewById(R.id.price);
            }
            if(rowLayout == R.layout.item_passenger) {
                ratingBar = itemView.findViewById(R.id.rating_bar);
            }
            if(rowLayout == R.layout.activity_item_driver_my_rides) {
                dateTextView = itemView.findViewById(R.id.Date);
                timeTextView = itemView.findViewById(R.id.Time);
                originTextView = itemView.findViewById(R.id.origin);
                destinationTextView = itemView.findViewById(R.id.destination);
                priceTextView = itemView.findViewById(R.id.price);
                ratingBar = itemView.findViewById(R.id.rating_bar);
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(rowLayout, parent, false);
        return new ViewHolder(v, rowLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Map<String, String> item = itemList.get(position);

        if (rowLayout == R.layout.item_driver) {
            Log.d("myAdapter", "Driver item: " + item);

            viewHolder.nameTextView.setText(item.get("Driver Name"));
            viewHolder.dateTextView.setText(item.get("rideDate"));
            viewHolder.timeTextView.setText(item.get("rideTime"));
            viewHolder.originTextView.setText("From: " + item.get("originLat") + ", " + item.get("originLng"));
            viewHolder.destinationTextView.setText("To: " + item.get("destinationLat") + ", " + item.get("destinationLng"));
            viewHolder.priceTextView.setText(item.get("price") + " denari");

            viewHolder.itemView.setOnClickListener(v -> {
                String driverName = itemList.get(position).get("Driver Name");
                Log.d("myAdapter", driverName);
                if (listener != null) {
                    listener.onDriverSelected(driverName);
                }
            });
        }
        if (rowLayout == R.layout.item_passenger) {

            // Get the name from the item map or use "Unknown" as default
            String name = item.getOrDefault("name", "Unknown");
            viewHolder.nameTextView.setText(name);

            // Set the current rating
            float currentRating = Float.parseFloat(item.getOrDefault("rating", "0"));
            viewHolder.ratingBar.setRating(currentRating);

            // Listener for RatingBar changes
            viewHolder.ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
                if (fromUser) {
                    String userName = item.get("name");

                    // Validate the userName
                    if (userName == null || userName.isEmpty()) {
                        Log.e("myAdapter", "Invalid or missing username.");
                        return;
                    }
                    db = mContext.openOrCreateDatabase("Carpooling", Context.MODE_PRIVATE, null);
                    // Ensure the database is available
                    if (db == null || !db.isOpen()) {
                        Log.e("myAdapter", "Database is not available.");
                        return;
                    }

                    // Perform the database query
                    Cursor c = null;
                    try {
                        c = db.rawQuery("SELECT id FROM users WHERE username=?", new String[]{userName});
                        if (c != null && c.moveToFirst()) {
                            int userId = c.getInt(c.getColumnIndexOrThrow("id"));
                            Log.d("myAdapter", "UserId: " + userId);

                            // Update the database with the new rating
                            if (userId != -1) {
                                updateRatingInDatabase(userId, rating);
                                Log.d("myAdapter", "Rating updated for userId: " + userId);
                                item.put("rating", String.valueOf(rating)); // Update the rating in the map
                            } else {
                                Log.e("myAdapter", "userID is invalid.");
                            }
                        } else {
                            Log.e("myAdapter", "No user found with username: " + userName);
                        }
                    } finally {
                        // Close the cursor
                        if (c != null) {
                            c.close();
                        }
                    }
                }
            });
        }

        if(rowLayout == R.layout.activity_item_driver_my_rides) {
            viewHolder.nameTextView.setText(item.get("Driver Name"));
            viewHolder.dateTextView.setText(item.get("rideDate"));
            viewHolder.timeTextView.setText(item.get("rideTime"));
            viewHolder.originTextView.setText("From: " + item.get("originLat") + ", " + item.get("originLng"));
            viewHolder.destinationTextView.setText("To: " + item.get("destinationLat") + ", " + item.get("destinationLng"));
            viewHolder.priceTextView.setText(item.get("price") + " denari");

            float currentRating = Float.parseFloat(item.getOrDefault("rating", "0"));
            viewHolder.ratingBar.setRating(currentRating);
            viewHolder.ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
                if (fromUser) {
                    String userIdStr = item.get("userID");
                    Log.d("myAdapter", ""+userIdStr);
                    if (userIdStr != null && !userIdStr.isEmpty()) {
                        int userId = Integer.parseInt(userIdStr);
                        Log.d("myAdapter", "before update");
                        updateRatingInDatabase(userId, rating);
                        Log.d("myAdapter", "after update");
                        item.put("rating", String.valueOf(rating));
                    } else {
                        Log.e("myAdapter", "userID is null or invalid.");
                    }
                }
            });
        }
    }

    private void updateRatingInDatabase(int userId, float newRating) {
        SQLiteDatabase db = mContext.openOrCreateDatabase("Carpooling", Context.MODE_PRIVATE, null);
        Cursor cursor = null;

        try {
            cursor = db.rawQuery("SELECT rating, num_ratings, total_rating FROM users WHERE id = ?", new String[]{String.valueOf(userId)});

            if (cursor != null && cursor.moveToFirst()) {
                int numRatings = cursor.getInt(cursor.getColumnIndexOrThrow("num_ratings"));
                float totalRating = cursor.getFloat(cursor.getColumnIndexOrThrow("total_rating"));

                // Calculate the new average rating
                float newAvgRating = (totalRating + newRating) / (numRatings + 1);

                // Update the user's rating data in the database
                db.execSQL("UPDATE users SET rating = ?, num_ratings = ?, total_rating = ? WHERE id = ?",
                        new Object[]{newAvgRating, numRatings + 1, totalRating + newRating, userId});
            }
        } catch (Exception e) {
            Log.e("myAdapter", "Error updating rating: ", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
