package com.example.carpoolingapp;

import android.content.Intent;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.database.Cursor;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class RegisterActivity extends AppCompatActivity {

    SQLiteDatabase db;
    EditText username, password, confirmPassword;
    Button registerButton;
    RadioGroup userTypeGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        db = openOrCreateDatabase("Carpooling", MODE_PRIVATE, null);
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS users(" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "username VARCHAR, " +
                        "password VARCHAR UNIQUE, " +
                        "user_type VARCHAR, " +
                        "vehicle VARCHAR, " +
                        "payment VARCHAR, " +
                        "rating FLOAT DEFAULT 0.0);"
        );

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.passwordConfirm);
        registerButton = findViewById(R.id.btn_register);
        userTypeGroup = findViewById(R.id.userType);

        registerButton.setOnClickListener(this::user_register);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void user_register(View view) {
        String usernameInput = username.getText().toString().trim();
        String passwordInput = password.getText().toString();
        String confirmPasswordInput = confirmPassword.getText().toString();
        int selectedTypeId = userTypeGroup.getCheckedRadioButtonId();
        RadioButton selectedUserType = findViewById(selectedTypeId);
        String userType = (selectedUserType != null) ? selectedUserType.getText().toString() : null;

        if (usernameInput.isEmpty()) {
            Toast.makeText(this, "Please enter a username.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (passwordInput.isEmpty() || confirmPasswordInput.isEmpty()) {
            Toast.makeText(this, "Please enter a password.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!passwordInput.equals(confirmPasswordInput)) {
            Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
            return;
        }

        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE username = ?", new String[]{usernameInput});
        if (cursor.moveToFirst()) {
            Toast.makeText(this, "Username already exists. Please choose another.", Toast.LENGTH_LONG).show();
            cursor.close();
            return;
        }
        cursor.close();

        try {
            db.execSQL(
                    "INSERT INTO users (username, password, user_type) VALUES (?, ?, ?)",
                    new Object[]{usernameInput, passwordInput, userType}
            );
            Toast.makeText(this, "User registered successfully!", Toast.LENGTH_SHORT).show();

            username.setText("");
            password.setText("");
            confirmPassword.setText("");
            userTypeGroup.clearCheck();
        } catch (Exception e) {
            Log.e("RegisterActivity", "Error registering user", e);
            Toast.makeText(this, "Error registering user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
