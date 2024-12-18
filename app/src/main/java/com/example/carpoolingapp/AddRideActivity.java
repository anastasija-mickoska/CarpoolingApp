package com.example.carpoolingapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class AddRideActivity extends AppCompatActivity {

    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ride);
        db = openOrCreateDatabase("Carpooling", MODE_PRIVATE, null);

        db.execSQL(
                "CREATE TABLE IF NOT EXISTS rides(" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "userID INTEGER, " +
                        "passengerID INTEGER, " +
                        "price FLOAT, " +
                        "originLat FLOAT, " +
                        "originLng FLOAT, " +
                        "destinationLat FLOAT, " +
                        "destinationLng FLOAT, " +
                        "rideDate DATE, " +
                        "rideTime TIME, " +
                        "FOREIGN KEY(userID) REFERENCES users(id), " +
                        "FOREIGN KEY(passengerID) REFERENCES users(id));");

        if (savedInstanceState == null) {
            CustomMapFragment mapFragment = new CustomMapFragment();

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragmentContainer, mapFragment);
            transaction.commit();
        }
    }

//    @Override
//    public void onLocationsPinned(LatLng origin, LatLng destination) {
//        if (origin != null && destination != null) {
//            FormFragment formFragment = FormFragment.newInstance(origin, destination);
//            getSupportFragmentManager()
//                    .beginTransaction()
//                    .replace(R.id.fragmentContainer, formFragment, "FORM_FRAGMENT_TAG")
//                    .commit();
//        } else {
//            // Log or handle the case where locations are null
//            Log.d("AddRideActivity", "Error: Origin or destination is null!");
//        }
//    }

    public void saveRideData(String price, String date, String time, double originLat, double originLng, double destLat, double destLng) {
       try {
           SQLiteDatabase db = openOrCreateDatabase("Carpooling", MODE_PRIVATE, null);
           db.execSQL(
                   "CREATE TABLE IF NOT EXISTS rides(" +
                           "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                           "userID INTEGER, " +
                           "passengerID INTEGER, " +
                           "price FLOAT, " +
                           "originLat FLOAT, " +
                           "originLng FLOAT, " +
                           "destinationLat FLOAT, " +
                           "destinationLng FLOAT, " +
                           "rideDate DATE, " +
                           "rideTime TIME, " +
                           "FOREIGN KEY(userID) REFERENCES users(id), " +
                           "FOREIGN KEY(passengerID) REFERENCES users(id));");

           String username = getIntent().getStringExtra("Driver Name");
           Log.d("SaveRideData", "username = " + username);
           Cursor cursor = db.rawQuery("SELECT id FROM users WHERE username = ?", new String[]{username});
           if (cursor.moveToFirst()) {
               int userId = cursor.getInt(0);
               Log.d("SaveRideData", "UserId: " + userId);

               String formattedDate = "";
               String formattedTime = "";
               if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                   DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault());
                   LocalDate parsedDate = LocalDate.parse(date, dateFormatter);
                   formattedDate = parsedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

                   DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault());
                   LocalTime parsedTime = LocalTime.parse(time, timeFormatter);
                   formattedTime = parsedTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                   Log.d("SaveRideData", "Formatted date: " + formattedDate + ", time: " + formattedTime);
               }
               Log.d("SaveRideData", "Inputs: price=" + price + ", date=" + date + ", time=" + time
                       + ", originLat=" + originLat + ", originLng=" + originLng
                       + ", destLat=" + destLat + ", destLng=" + destLng);
               String query = "INSERT INTO rides (userID, price, originLat, originLng, destinationLat, destinationLng, rideDate, rideTime) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
               db.execSQL(query, new Object[]{
                       userId, Float.parseFloat(price),
                       originLat, originLng,
                       destLat, destLng,
                       formattedDate, formattedTime
               });

               Toast.makeText(this, "Ride saved successfully!", Toast.LENGTH_SHORT).show();
           } else {
               Toast.makeText(this, "User not found in the database.", Toast.LENGTH_SHORT).show();
           }

           cursor.close();
           db.close();
       }
       catch(Exception e) {
           Toast.makeText(this, "Error saving locations: " + e.getMessage(), Toast.LENGTH_LONG).show();
           Log.d("SaveRideData",e.getMessage());
       }
    }
}

