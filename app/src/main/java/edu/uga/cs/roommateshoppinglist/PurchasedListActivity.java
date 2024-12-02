package edu.uga.cs.roommateshoppinglist;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class PurchasedListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_purchased_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // sets up action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        // set up floating action button
        FloatingActionButton settleCostsFab = findViewById(R.id.settleCostsFab);
        settleCostsFab.setOnClickListener(new PurchasedListActivity.FloatingSettleButtonClickListener());
    }


    /*
    Opens a dialog fragment to add a new item to the shopping list.
     */
    private class FloatingSettleButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            // settle costs
            Toast.makeText(PurchasedListActivity.this, "Settling costs...", Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * Sets up back button in purchased list activity.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getOnBackPressedDispatcher().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}