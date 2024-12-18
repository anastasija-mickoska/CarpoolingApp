package com.example.carpoolingapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DriverPassengersActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    myAdapter passengerAdapter;
    List<Map<String, String>> passengersList;
    Button add;
    Button logout;
    String username;
    int userId;
    int passengerId;
    String passengerName;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_driver_passengers);
        username = getIntent().getStringExtra("username");
        recyclerView = findViewById(R.id.passengers_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        passengersList = fetchPassengersFromQuery(username);
        passengerAdapter = new myAdapter(passengersList, R.layout.item_passenger, this);
        recyclerView.setAdapter(passengerAdapter);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        TextView user = findViewById(R.id.welcome1);
        user.setText("Welcome, " + username);

        logout = findViewById(R.id.logout_btn);
        logout.setOnClickListener(v -> {
            Intent intent = new Intent(DriverPassengersActivity.this, MainActivity.class);
            startActivity(intent);
        });
        add = findViewById(R.id.add_ride);
        add.setOnClickListener(v -> {
            Intent intent1 = new Intent(DriverPassengersActivity.this, AddRideActivity.class);
            intent1.putExtra("Driver Name", username);
            startActivity(intent1);
        });
    }

    private List<Map<String, String>> fetchPassengersFromQuery(String username) {
        List<Map<String, String>> passengers = new ArrayList<>();
        SQLiteDatabase db = openOrCreateDatabase("Carpooling", MODE_PRIVATE, null);
        Cursor cursor = null;
        Cursor cursor1 = null;
        Cursor cursor2 = null;

        try {
            // Query to get the userId for the given username
            cursor = db.rawQuery("SELECT id FROM users WHERE username=?", new String[]{username});
            if (cursor != null && cursor.moveToFirst()) {
                int userId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));

                // Query to get passenger IDs for the user's rides
                cursor1 = db.rawQuery("SELECT passengerID FROM rides WHERE userID=?", new String[]{String.valueOf(userId)});
                if (cursor1 != null && cursor1.moveToFirst()) {
                    do {
                        int passengerId = cursor1.getInt(cursor1.getColumnIndexOrThrow("passengerID"));

                        // Query to get the passenger's username
                        cursor2 = db.rawQuery("SELECT username FROM users WHERE id=?", new String[]{String.valueOf(passengerId)});
                        if (cursor2 != null && cursor2.moveToFirst()) {
                            String passengerName = cursor2.getString(cursor2.getColumnIndexOrThrow("username"));

                            // Create a map with only the passenger's name
                            Map<String, String> passenger = new HashMap<>();
                            passenger.put("name", passengerName);
                            passengers.add(passenger);
                        }
                    } while (cursor1.moveToNext());
                }
            }
        } finally {
            // Close cursors and database
            if (cursor != null) {
                cursor.close();
            }
            if (cursor1 != null) {
                cursor1.close();
            }
            if (cursor2 != null) {
                cursor2.close();
            }
            db.close();
        }

        return passengers;
    }

}