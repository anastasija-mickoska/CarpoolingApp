package com.example.carpoolingapp;

import android.os.Bundle;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class item_driver extends AppCompatActivity {
    private TextView nameTextView, dateTextView, timeTextView, originTextView, destinationTextView, priceTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_driver); // your layout file for drivers

        // Initialize views
        nameTextView = findViewById(R.id.Name);
        dateTextView = findViewById(R.id.Date);
        timeTextView = findViewById(R.id.Time);
        originTextView = findViewById(R.id.origin);
        destinationTextView = findViewById(R.id.destination);
        priceTextView = findViewById(R.id.price);

        String driverName = getIntent().getStringExtra("DriverName");
        String rideDate = getIntent().getStringExtra("RideDate");
        String rideTime = getIntent().getStringExtra("RideTime");
        String origin = getIntent().getStringExtra("Origin");
        String destination = getIntent().getStringExtra("Destination");
        String price = getIntent().getStringExtra("Price");

        // Set data on UI components
        nameTextView.setText(driverName);
        dateTextView.setText(rideDate);
        timeTextView.setText(rideTime);
        originTextView.setText("From: " + origin);
        destinationTextView.setText("To: " + destination);
        priceTextView.setText(price);
    }
}
