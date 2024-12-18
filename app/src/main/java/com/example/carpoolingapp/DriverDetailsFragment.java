package com.example.carpoolingapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;
import android.widget.Toast;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;


public class DriverDetailsFragment extends Fragment {
    private TextView driverNameTextView;
    private TextView priceTextView;
    private TextView originTextView;
    private TextView destinationTextView;
    private TextView rideDateTextView;
    private TextView rideTimeTextView;
    private TextView ratingTextView;
    String driverName, price, originLat, originLng, destinationLat, destinationLng, rideDate, rideTime;
    SQLiteDatabase db;

    public DriverDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("DriverDetailsFragment", "onCreate");
        if (getArguments() != null) {
            driverName = getArguments().getString("driverName");
            Log.d("DriverDetailsFragment", "driverId:" + driverId);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("DriverDetailsFragment", "onCreateView: Started");

        View view = inflater.inflate(R.layout.fragment_driver_details, container, false);

        // Initialize your views
        driverNameTextView = view.findViewById(R.id.Name);
        priceTextView = view.findViewById(R.id.price);
        originTextView = view.findViewById(R.id.origin);
        destinationTextView = view.findViewById(R.id.destination);
        rideDateTextView = view.findViewById(R.id.Date);
        rideTimeTextView = view.findViewById(R.id.Time);
        ratingTextView = view.findViewById(R.id.rating);

        // Retrieve the data passed from the previous fragment
        Bundle args = getArguments();
        if (args != null) {
             driverName = args.getString("driverName");
             price = args.getString("price");
             originLat = args.getString("originLat");
             originLng = args.getString("originLng");
             destinationLat = args.getString("destinationLat");
             destinationLng = args.getString("destinationLng");
             rideDate = args.getString("rideDate");
             rideTime = args.getString("rideTime");
            String rating = args.getString("rating");

            // Set the values to the TextViews
            driverNameTextView.setText(driverName);
            priceTextView.setText("Price: " + price);
            originTextView.setText("From: " + originLat + ", " + originLng);
            destinationTextView.setText("To: " + destinationLat + ", " + destinationLng);
            rideDateTextView.setText("Date: " + rideDate);
            rideTimeTextView.setText("Time: " + rideTime);
            ratingTextView.setText("Rating: " + rating);
        }

        Log.d("DriverDetailsFragment", "onCreateView: Finished setting data");

        return view;
    }
    String passengerUsername;
    int passengerId;
    int driverId;
    @SuppressLint({"Range", "NewApi"})
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("DriverDetailsFragment", "onViewCreated called");
        // Retrieve the passenger username passed from the previous fragment
        Bundle bundle = getArguments();
        if (bundle != null) {
            passengerUsername = bundle.getString("passengerUsername");
        }


        Button chooseDriverBtn = view.findViewById(R.id.chooseDriver);
        chooseDriverBtn.setOnClickListener(v -> {
            SQLiteDatabase db = null;
            Cursor cursor = null;
            Cursor cursor1 = null;
            Cursor cursorDriver = null;
            try {
                // Open the database
                db = getActivity().openOrCreateDatabase("Carpooling", Context.MODE_PRIVATE, null);

                // Debugging: Print the values being passed to the query
                Log.d("ChooseDriver", "driverId: " + driverId);
                Log.d("ChooseDriver", "price: " + price);
                Log.d("ChooseDriver", "originLat: " + originLat);
                Log.d("ChooseDriver", "originLng: " + originLng);
                Log.d("ChooseDriver", "destinationLat: " + destinationLat);
                Log.d("ChooseDriver", "destinationLng: " + destinationLng);
                Log.d("ChooseDriver", "rideDate: " + rideDate);
                Log.d("ChooseDriver", "rideTime: " + rideTime);
                Log.d("ChooseDriver", "driverName: " + driverName);

                // Retrieve the driver ID based on the driver name
                cursorDriver = db.rawQuery("SELECT id FROM users WHERE username=?", new String[]{driverName});
                if (cursorDriver != null && cursorDriver.moveToFirst()) {
                    driverId = cursorDriver.getInt(cursorDriver.getColumnIndex("id"));
                }
                if (cursorDriver != null) {
                    cursorDriver.close();
                }

                // Select the ride ID based on various conditions and check for future rides
                String query = "SELECT id, rideDate, rideTime FROM rides WHERE userID=? AND price=?";
                Log.d("ChooseDriver", "Query: " + query);
                cursor = db.rawQuery(query, new String[]{String.valueOf(driverId), price + ".0"});

                // Get the current date and time
                LocalDate currentDate = LocalDate.now();
                LocalTime currentTime = LocalTime.now();

                Log.d("ChooseDriver", "DriverId:" + String.valueOf(driverId));
                boolean rideFound = false;

                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        String rideDate = cursor.getString(cursor.getColumnIndex("rideDate"));
                        String rideTime = cursor.getString(cursor.getColumnIndex("rideTime"));

                        Log.d("ChooseDriver", "Processing rideDate: " + rideDate + " and rideTime: " + rideTime);

                        if (rideDate != null && !rideDate.isEmpty() && rideTime != null && !rideTime.isEmpty()) {
                            try {
                                // Parse the ride date and time
                                @SuppressLint({"NewApi", "LocalSuppress"}) LocalDate parsedRideDate = LocalDate.parse(rideDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                                @SuppressLint({"NewApi", "LocalSuppress"}) LocalTime parsedRideTime = LocalTime.parse(rideTime, DateTimeFormatter.ofPattern("HH:mm:ss"));

                                // Only consider future rides
                                if (parsedRideDate.isAfter(currentDate) || (parsedRideDate.isEqual(currentDate) && parsedRideTime.isAfter(currentTime))) {
                                    Log.d("ChooseDriver", "Future ride found");

                                    // Ride is valid, now update it with the passenger ID
                                    int rideId = cursor.getInt(cursor.getColumnIndex("id"));
                                    cursor1 = db.rawQuery("SELECT id FROM users WHERE username=?", new String[]{passengerUsername});
                                    if (cursor1 != null && cursor1.moveToFirst()) {
                                        passengerId = cursor1.getInt(cursor1.getColumnIndex("id"));
                                        Log.d("ChooseDriver", "PassengerId: " + passengerId);
                                    }

                                    if (passengerId != -1) {
                                        String updateQuery = "UPDATE rides SET passengerId = ? WHERE id = ?";
                                        db.execSQL(updateQuery, new Object[]{passengerId, rideId});
                                        Toast.makeText(getContext(), "Driver chosen successfully!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getContext(), "No matching passenger found!", Toast.LENGTH_SHORT).show();
                                    }
                                    rideFound = true;
                                    break; // Stop after finding the first valid future ride
                                }
                            } catch (Exception e) {
                                Log.e("ChooseDriver", "Error parsing ride date or time.", e);
                            }
                        }
                    }

                    if (!rideFound) {
                        Toast.makeText(getContext(), "No matching future ride found!", Toast.LENGTH_SHORT).show();
                    }
                    cursor.close();
                }
            } catch (Exception e) {
                Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("ChooseDriver", e.getMessage());
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
                if (cursor1 != null) {
                    cursor1.close();
                }
                if (cursorDriver != null) {
                    cursorDriver.close();
                }
                if (db != null) {
                    db.close();
                }
            }
        });

    }

}
