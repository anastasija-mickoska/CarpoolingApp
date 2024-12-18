package com.example.carpoolingapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class PassengerActivity extends AppCompatActivity implements OnDriverSelectedListener {

    SQLiteDatabase db;
    RecyclerView recyclerView;
    myAdapter adapter;
    Button logout_button;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_passenger);

        if (savedInstanceState == null) {
            // Always add the DriversListFragment to the list_fragment_container
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.list_fragment_container, new DriversListFragment())
                    .commit();

            // Check if details_fragment_container exists before adding the DriverDetailsFragment
            if (findViewById(R.id.details_fragment_container) != null) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.details_fragment_container, new DriverDetailsFragment());
                transaction.commit();
            }
        }
    }


    @Override
    public void onDriverSelected(String driverId) {
        Log.d("PassengerActivity", driverId);
        DriverDetailsFragment detailsFragment = new DriverDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("driverId", driverId);
        detailsFragment.setArguments(bundle);

        if (findViewById(R.id.details_fragment_container) != null) {
            // Landscape: Replace details_fragment_container
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.details_fragment_container, detailsFragment)
                    .commit();
        } else {
            // Portrait: Replace list_fragment_container and add to back stack
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.list_fragment_container, detailsFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

}
