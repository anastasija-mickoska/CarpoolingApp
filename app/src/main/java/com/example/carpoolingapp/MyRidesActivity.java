package com.example.carpoolingapp;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.RatingBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyRidesActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    myAdapter rideAdapter;
    List<Map<String, String>> ridesList;
    int passengerId;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_rides);

        recyclerView = findViewById(R.id.myRidesList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        String passengerName = getIntent().getStringExtra("passengerName");
        if (passengerName != null) {
            SQLiteDatabase db = openOrCreateDatabase("Carpooling", MODE_PRIVATE, null);
            Cursor cursor = db.rawQuery("SELECT id FROM users WHERE username=?", new String[]{passengerName});
            if(cursor!= null && cursor.moveToFirst()) {
                passengerId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            }
            ridesList = fetchRidesFromQuery(passengerId);
        }
        rideAdapter = new myAdapter(ridesList, R.layout.activity_item_driver_my_rides, this);
        recyclerView.setAdapter(rideAdapter);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private List<Map<String, String>> fetchRidesFromQuery(int passengerId) {
        List<Map<String, String>> ridesList = new ArrayList<>();
        SQLiteDatabase db = openOrCreateDatabase("Carpooling", MODE_PRIVATE, null);
        if (db == null) {
            throw new NullPointerException("Database not found");
        }

        // Query for rides where the passengerId matches
        Cursor cursor = db.rawQuery("SELECT userID, price, originLat, originLng, destinationLat, destinationLng, rideDate, rideTime " +
                "FROM rides WHERE passengerID = ?", new String[]{String.valueOf(passengerId)});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Map<String, String> ride = new HashMap<>();
                String userID = cursor.getString(cursor.getColumnIndexOrThrow("userID"));
                Cursor userCursor = db.rawQuery("SELECT username FROM users WHERE id=?", new String[]{userID});
                String driverName = ""; // Default value if no match is found
                if (userCursor != null && userCursor.moveToFirst()) {
                    driverName = userCursor.getString(userCursor.getColumnIndexOrThrow("username"));
                    userCursor.close(); // Close the cursor after use
                }
                ride.put("userID", userID);
                ride.put("Driver Name", driverName);
                ride.put("rideDate", cursor.getString(cursor.getColumnIndexOrThrow("rideDate")));
                ride.put("rideTime", cursor.getString(cursor.getColumnIndexOrThrow("rideTime")));
                ride.put("originLat", cursor.getString(cursor.getColumnIndexOrThrow("originLat")));
                ride.put("originLng", cursor.getString(cursor.getColumnIndexOrThrow("originLng")));
                ride.put("destinationLat", cursor.getString(cursor.getColumnIndexOrThrow("destinationLat")));
                ride.put("destinationLng", cursor.getString(cursor.getColumnIndexOrThrow("destinationLng")));
                ride.put("price", cursor.getString(cursor.getColumnIndexOrThrow("price")));
                ridesList.add(ride);
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }
        db.close();

        return ridesList;
    }

}