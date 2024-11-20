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

public class LoginActivity extends AppCompatActivity {

    private Button loginB;
    private Button backToRegisterB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loginB = findViewById(R.id.loginB);
        backToRegisterB = findViewById(R.id.backToRegisterB);

        // Set click listeners
        loginB.setOnClickListener(new LoginActivity.LoginButtonClickListener());
        backToRegisterB.setOnClickListener(new LoginActivity.BackToRegisterButtonClickListener());

    }

    private class LoginButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            // ** INSERT FIREBASE ON CLICK LOGIN IMPLEMENTATION HERE **
            // (if needed)

            Intent intent = new Intent(view.getContext(), ListActivity.class);
            startActivity(intent); // start the new activity
            finish();

        }
    }

    private class BackToRegisterButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            // ** INSERT FIREBASE ON CLICK LOGIN IMPLEMENTATION HERE **
            // (if needed)

            Intent intent = new Intent(view.getContext(), RegistrationActivity.class);
            startActivity(intent); // start the new activity
            finish();
        }
    }
}