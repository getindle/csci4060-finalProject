package edu.uga.cs.roommateshoppinglist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

public class RegistrationActivity extends AppCompatActivity {

    private Button loginB;
    private Button registerB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        loginB = findViewById(R.id.loginB);
        registerB = findViewById(R.id.registerB);

        // Set click listeners
        loginB.setOnClickListener(new LoginButtonClickListener());
        registerB.setOnClickListener(new RegisterButtonClickListener());

    }

    private class LoginButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(view.getContext(), LoginActivity.class);
            startActivity(intent); // Start the new activity
            finish();
        }
    }

    private class RegisterButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            // ** INSERT ON CLICK REGISTRATION IMPLEMENTATION HERE **


            Intent intent = new Intent(view.getContext(), ListActivity.class);
            startActivity(intent);
            finish();
        }
    }
}