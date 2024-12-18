package com.example.carpoolingapp;

import android.os.Bundle;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class item_passenger extends AppCompatActivity {
    private TextView nameTextView;
    private RatingBar ratingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_passenger); // your layout file for passengers

        nameTextView = findViewById(R.id.Name);
        ratingBar = findViewById(R.id.rating_bar);

        String name = getIntent().getStringExtra("PassengerName");
        float rating = getIntent().getFloatExtra("PassengerRating", 0); // Default to 0 if no rating is set

        nameTextView.setText(name);
        ratingBar.setRating(rating);

    }
}
