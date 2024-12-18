package com.example.carpoolingapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class DriverActivity extends AppCompatActivity {
SQLiteDatabase db;
EditText vehicleName;
EditText paymentDetails;
String username;
Button driverDetails;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_driver);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        username = getIntent().getStringExtra("username");

        TextView welcomeMessage = findViewById(R.id.driver_welcome);
        welcomeMessage.setText("Welcome, Driver " + username + "!");

        vehicleName = findViewById(R.id.vehicle);
        paymentDetails = findViewById(R.id.payment);
        driverDetails = findViewById(R.id.submit_driver_details);
        db = openOrCreateDatabase("Carpooling", MODE_PRIVATE, null);

        driverDetails.setOnClickListener(this::insert_driver_details);

    }
    public void insert_driver_details(View view) {
        String vehicleInput = vehicleName.getText().toString().trim();
        String paymentInput = paymentDetails.getText().toString().trim();
        String usernameInput = getIntent().getStringExtra("username");

        if (vehicleInput.isEmpty() || paymentInput.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            return;
        }
        Cursor cursor = db.rawQuery("SELECT id FROM users WHERE username = ?", new String[]{usernameInput});
        if (cursor.moveToFirst()) {
            int userId = cursor.getInt(0);
            // Insert the vehicle and payment details
            db.execSQL("UPDATE users SET vehicle = ?, payment = ? WHERE id = ?",
                    new Object[]{vehicleInput, paymentInput, userId});

            Toast.makeText(this, "Details successfully added!", Toast.LENGTH_SHORT).show();
            vehicleName.setText("");
            paymentDetails.setText("");
            cursor.close();

            Intent intent = new Intent(DriverActivity.this, DriverPassengersActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
        } else {
            Toast.makeText(this, "User not found.", Toast.LENGTH_SHORT).show();
        }
    }
}