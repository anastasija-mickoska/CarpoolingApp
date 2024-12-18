package com.example.carpoolingapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    SQLiteDatabase db;
    EditText usernameField, passwordField;
    Button loginButton;
    String vehicle, payment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = openOrCreateDatabase("Carpooling", MODE_PRIVATE, null);
        usernameField = findViewById(R.id.username);
        passwordField = findViewById(R.id.password);
        loginButton = findViewById(R.id.btn_login);
        // Set click listener for login button
        loginButton.setOnClickListener(this::login_user);
        TextView registerTextView = findViewById(R.id.register_question);
        registerTextView.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
    public void login_user(View view) {
        String usernameInput = usernameField.getText().toString().trim();
        String passwordInput = passwordField.getText().toString();

        if (usernameInput.isEmpty() || passwordInput.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            return;
        }
        Cursor cursor = db.rawQuery("SELECT password, user_type FROM users WHERE username = ?", new String[]{usernameInput});

        if (cursor.moveToFirst()) {
            String storedPassword = cursor.getString(0);
            String userType = cursor.getString(1);

            // Compare the passwords
            if (storedPassword.equals(passwordInput)) {
                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();

                // Navigate to the appropriate activity based on user type
                if ("Driver".equalsIgnoreCase(userType)) {
                    Cursor cursor1 = db.rawQuery("SELECT vehicle, payment FROM users WHERE username=?", new String[]{usernameInput});
                    if(cursor1!=null && cursor1.moveToFirst()) {
                        vehicle = cursor1.getString(cursor1.getColumnIndexOrThrow("vehicle"));
                        payment = cursor1.getString(cursor1.getColumnIndexOrThrow("payment"));
                        if(vehicle != null && payment != null) {
                            Intent intent2 = new Intent(MainActivity.this, DriverPassengersActivity.class);
                            intent2.putExtra("username", usernameInput);
                            startActivity(intent2);
                        }
                        else {
                            Intent intent = new Intent(MainActivity.this, DriverActivity.class);
                            intent.putExtra("username", usernameInput); // Pass username to the activity
                            startActivity(intent);
                        }
                    }
                } else if ("Passenger".equalsIgnoreCase(userType)) {
                    Intent intent1 = new Intent(MainActivity.this, PassengerActivity.class);
                    intent1.putExtra("username", usernameInput); // Pass username to the activity
                    startActivity(intent1);
                } else {
                    Toast.makeText(this, "Invalid user type.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Incorrect password. Please try again.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Username not found.", Toast.LENGTH_SHORT).show();
        }

        cursor.close();
    }
}
