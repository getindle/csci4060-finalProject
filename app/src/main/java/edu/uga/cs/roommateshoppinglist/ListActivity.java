package edu.uga.cs.roommateshoppinglist;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ListActivity extends AppCompatActivity {

    // instance variables to set up hamburger icon
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // create ActionBar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // create drawer
        drawerLayout = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState(); // sync state so "hamburger icon" shows instead of "back arrow"

        // inflate list with button options
        String[] drawerItems = {"Recently Purchased", "Button 2", "Help"};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, drawerItems);
        ListView drawerList = findViewById(R.id.drawer_list);
        drawerList.setAdapter(arrayAdapter);

        // implement "on click" actions for when a hamburger option is clicked
        drawerList.setOnItemClickListener((parent, view, position, id) -> {
            switch (position) {
                case 0: // RECENTLY PURCHASED OPTION
                    // [RECENTLY PURCHASED OPTION LOGIC]
                    break;
                case 1: // BUTTON 2
                    // [BUTTON 2 LOGIC GOES HERE]
                    break;
                case 2: // HELP OPTION
                    // [HELP LOGIC GOES HERE]
                    break;
            }
            drawerLayout.closeDrawers(); // Close the drawer
        });

        // set up floating action button
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new ListActivity.FloatingActionButtonClickListener());
    }

    private class FloatingActionButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Toast.makeText(ListActivity.this, "FAB clicked", Toast.LENGTH_SHORT).show();
            // ADD FLOATING ACTION BUTTON "ON CLICK" HERE
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}