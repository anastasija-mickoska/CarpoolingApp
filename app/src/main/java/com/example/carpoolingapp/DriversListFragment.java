package com.example.carpoolingapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.service.autofill.TextValueSanitizer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class DriversListFragment extends Fragment implements OnDriverSelectedListener {
    private SQLiteDatabase db;
    private RecyclerView recyclerView;
    private myAdapter adapter;
    private Button logout_button;
    private Button myRidesBtn;
    private OnDriverSelectedListener listener;
    private TextView welcome;
    String username;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnDriverSelectedListener) {
            listener = (OnDriverSelectedListener) context;
            Log.d("DriversListFragment", "onAttach: OnDriverSelectedListener implemented");
        } else {
            throw new RuntimeException(context.toString() + " must implement OnDriverSelectedListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("DriversListFragment", "onCreateView: Started");

        View view = inflater.inflate(R.layout.fragment_drivers_list, container, false);
        welcome = view.findViewById(R.id.passenger_welcome);

        // Retrieve username and set welcome message
        username = getActivity().getIntent().getStringExtra("username");
        welcome.setText("Welcome, " + username);
        Log.d("DriversListFragment", "onCreateView: Welcome message set for " + username);

        recyclerView = view.findViewById(R.id.drivers_list);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        List<Map<String, String>> driversList = fetchDriversFromQuery();
        Log.d("DriversListFragment", "onCreateView: Fetched " + driversList.size() + " drivers");

        adapter = new myAdapter(driversList, R.layout.item_driver, requireContext());
        adapter.setOnDriverSelectedListener(this);
        recyclerView.setAdapter(adapter);

        logout_button = view.findViewById(R.id.logout_btn);
        logout_button.setOnClickListener(v -> {
            Log.d("DriversListFragment", "Logout button clicked");
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
        });
        myRidesBtn = view.findViewById(R.id.myRides);
        myRidesBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MyRidesActivity.class);
            intent.putExtra("passengerName", username);
            startActivity(intent);
        });

        Log.d("DriversListFragment", "onCreateView: Finished");
        return view;
    }

    @SuppressLint("Range")
    private List<Map<String, String>> fetchDriversFromQuery() {
        Log.d("DriversListFragment", "fetchDriversFromQuery: Started");
        List<Map<String, String>> drivers = new ArrayList<>();
        db = requireContext().openOrCreateDatabase("Carpooling", Context.MODE_PRIVATE, null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDate currentDate = LocalDate.now();
            LocalTime currentTime = LocalTime.now();

            // Query to select rides
            Cursor cursor = db.rawQuery("SELECT userID, price, originLat, originLng, destinationLat, destinationLng, rideDate, rideTime FROM rides", null);
            Log.d("DriversListFragment", "fetchDriversFromQuery: Cursor query executed");

            if (cursor!= null && cursor.moveToFirst()) {
                do {
                    // Retrieve ride data with null checks
                    String rideDate = cursor.getString(cursor.getColumnIndexOrThrow("rideDate"));
                    String rideTime = cursor.getString(cursor.getColumnIndexOrThrow("rideTime"));

                    Log.d("DriversListFragment", "fetchDriversFromQuery: Processing rideDate " + rideDate + " and rideTime " + rideTime);

                    // Check if rideDate and rideTime are not null or empty
                    if (rideDate != null && !rideDate.isEmpty() && rideTime != null && !rideTime.isEmpty()) {
                        try {
                            // Parse the ride date and time
                            LocalDate parsedRideDate = LocalDate.parse(rideDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                            LocalTime parsedRideTime = LocalTime.parse(rideTime, DateTimeFormatter.ofPattern("HH:mm:ss"));

                            // Only consider future rides
                            if (parsedRideDate.isAfter(currentDate) || (parsedRideDate.isEqual(currentDate) && parsedRideTime.isAfter(currentTime))) {
                                Log.d("DriversListFragment", "fetchDriversFromQuery: Future ride found");

                                int userId = cursor.getInt(cursor.getColumnIndexOrThrow("userID"));
                                Cursor cursor1 = db.rawQuery("SELECT rating FROM users WHERE id=?", new String[]{String.valueOf(userId)});
                                float rating = 0.0f;

                                if (cursor1 != null && cursor1.moveToFirst()) {
                                    rating = cursor1.getFloat(cursor1.getColumnIndex("rating"));
                                    cursor1.close();
                                }

                                // Query to get the driver's username
                                Cursor userCursor = db.rawQuery("SELECT username FROM users WHERE id = ?", new String[]{String.valueOf(userId)});
                                if (userCursor.moveToFirst()) {
                                    String driverName = userCursor.getString(userCursor.getColumnIndexOrThrow("username"));

                                    Map<String, String> driver = new HashMap<>();
                                    driver.put("Driver Name", driverName);
                                    driver.put("price", cursor.getString(cursor.getColumnIndexOrThrow("price")));
                                    driver.put("originLat", cursor.getString(cursor.getColumnIndexOrThrow("originLat")));
                                    driver.put("originLng", cursor.getString(cursor.getColumnIndexOrThrow("originLng")));
                                    driver.put("destinationLat", cursor.getString(cursor.getColumnIndexOrThrow("destinationLat")));
                                    driver.put("destinationLng", cursor.getString(cursor.getColumnIndexOrThrow("destinationLng")));
                                    driver.put("rideDate", rideDate);
                                    driver.put("rideTime", rideTime);
                                    driver.put("rating", String.valueOf(rating));  // Add rating to the map

                                    drivers.add(driver);
                                    Log.d("DriversListFragment", "fetchDriversFromQuery: Driver added " + driverName);
                                }
                                userCursor.close();
                            }
                        } catch (Exception e) {
                            // Log the error and continue with empty values
                            Log.e("DriversListFragment", "Error parsing ride date or time. Ride Date: " + rideDate + ", Ride Time: " + rideTime, e);
                        }
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        db.close();
        Log.d("DriversListFragment", "fetchDriversFromQuery: Finished with " + drivers.size() + " drivers");
        return drivers;
    }

    @SuppressLint("Range")
    private Map<String, String> fetchDriverByUserId(String driverUsername) {
        Log.d("DriversListFragment", "fetchDriverByUserId: Started for driverUsername " + driverUsername);
        Map<String, String> driver = null;
        db = requireContext().openOrCreateDatabase("Carpooling", Context.MODE_PRIVATE, null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDate currentDate = LocalDate.now();
            LocalTime currentTime = LocalTime.now();

            // Fetch userId from the username
            Cursor userIdCursor = db.rawQuery("SELECT id FROM users WHERE username=?", new String[]{driverUsername});
            int userId = -1;
            if (userIdCursor != null && userIdCursor.moveToFirst()) {
                userId = userIdCursor.getInt(userIdCursor.getColumnIndex("id"));
                userIdCursor.close();
            }

            // Fetch all rides associated with the user
            Cursor cursor = db.rawQuery("SELECT userID, price, originLat, originLng, destinationLat, destinationLng, rideDate, rideTime FROM rides WHERE userID = ?", new String[]{String.valueOf(userId)});

            // Loop through the rides and process each one
            while (cursor.moveToNext()) {
                // Retrieve ride data with null checks
                String rideDate = cursor.getString(cursor.getColumnIndexOrThrow("rideDate"));
                String rideTime = cursor.getString(cursor.getColumnIndexOrThrow("rideTime"));

                Log.d("DriversListFragment", "fetchDriverByUserId: Processing rideDate " + rideDate + " and rideTime " + rideTime);

                if (rideDate != null && !rideDate.isEmpty() && rideTime != null && !rideTime.isEmpty()) {
                    try {
                        // Parse the ride date and time
                        LocalDate parsedRideDate = LocalDate.parse(rideDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                        LocalTime parsedRideTime = LocalTime.parse(rideTime, DateTimeFormatter.ofPattern("HH:mm:ss"));

                        // Only consider future rides
                        if (parsedRideDate.isAfter(currentDate) || (parsedRideDate.isEqual(currentDate) && parsedRideTime.isAfter(currentTime))) {
                            Log.d("DriversListFragment", "fetchDriverByUserId: Future ride found");

                            // Query to get the driver's username (assuming the username is the same for the driver)
                            Cursor userCursor = db.rawQuery("SELECT username FROM users WHERE id = ?", new String[]{String.valueOf(userId)});
                            if (userCursor.moveToFirst()) {
                                String driverName = userCursor.getString(userCursor.getColumnIndexOrThrow("username"));

                                // Query to get the driver's rating
                                Cursor ratingCursor = db.rawQuery("SELECT rating FROM users WHERE id = ?", new String[]{String.valueOf(userId)});
                                float rating = 0.0f;
                                if (ratingCursor != null && ratingCursor.moveToFirst()) {
                                    rating = ratingCursor.getFloat(ratingCursor.getColumnIndex("rating"));
                                    ratingCursor.close();
                                }

                                // Store driver details in the map if it's the first ride found
                                if (driver == null) {
                                    driver = new HashMap<>();
                                    driver.put("Driver Name", driverName);
                                    driver.put("price", cursor.getString(cursor.getColumnIndexOrThrow("price")));
                                    driver.put("originLat", cursor.getString(cursor.getColumnIndexOrThrow("originLat")));
                                    driver.put("originLng", cursor.getString(cursor.getColumnIndexOrThrow("originLng")));
                                    driver.put("destinationLat", cursor.getString(cursor.getColumnIndexOrThrow("destinationLat")));
                                    driver.put("destinationLng", cursor.getString(cursor.getColumnIndexOrThrow("destinationLng")));
                                    driver.put("rideDate", rideDate);
                                    driver.put("rideTime", rideTime);
                                    driver.put("rating", String.valueOf(rating)); // Add rating to the map
                                    Log.d("DriversListFragment", "fetchDriverByUserId: Driver added " + driverName);
                                }
                            }
                            userCursor.close();
                        }
                    } catch (Exception e) {
                        // Log the error
                        Log.e("DriversListFragment", "Error parsing ride date or time. Ride Date: " + rideDate + ", Ride Time: " + rideTime, e);
                    }
                }
            }
            cursor.close();
        }
        db.close();
        Log.d("DriversListFragment", "fetchDriverByUserId: Finished");
        return driver;
    }


    @Override
    public void onDriverSelected(String driverName) {
        Log.d("DriversListFragment", "onDriverSelected: Driver selected with driverNAME " + driverName);

        Map<String, String> selectedDriver = fetchDriverByUserId(driverName);

        if (selectedDriver != null) {
            Log.d("DriversListFragment", "onDriverSelected: Driver found, passing data to listener");

            DriverDetailsFragment driverDetailsFragment = new DriverDetailsFragment();

            // Bundle to pass data to the next fragment
            Bundle bundle = new Bundle();
            bundle.putString("passengerUsername", username);
            bundle.putString("driverName", selectedDriver.get("Driver Name"));
            bundle.putString("price", selectedDriver.get("price"));
            bundle.putString("originLat", selectedDriver.get("originLat"));
            bundle.putString("originLng", selectedDriver.get("originLng"));
            bundle.putString("destinationLat", selectedDriver.get("destinationLat"));
            bundle.putString("destinationLng", selectedDriver.get("destinationLng"));
            bundle.putString("rideDate", selectedDriver.get("rideDate"));
            bundle.putString("rideTime", selectedDriver.get("rideTime"));
            bundle.putString("rating", selectedDriver.get("rating"));  // Pass rating as a string

            driverDetailsFragment.setArguments(bundle);

            int orientation = getResources().getConfiguration().orientation;

            if (orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT) {
                // Portrait: Replace the current fragment with DriverDetailsFragment
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.list_fragment_container, driverDetailsFragment)
                        .addToBackStack(null)
                        .commit();
            } else if (orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE) {
                // Landscape: Display both fragments side by side
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.details_fragment_container, driverDetailsFragment)
                        .commit();
            }
        }
    }


}