package edu.uga.cs.roommateshoppinglist;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class PurchasedListActivity extends AppCompatActivity implements ItemRecyclerViewAdapter.PurchasedListListener {

    public static final String TAG = "PurchasedListActivity";

    private DatabaseReference purchasedListRef;
    private DatabaseReference shoppingListRef;
    private FirebaseDatabase database;
    private List<Item> purchasedList;
    private ArrayList<Item> removeList;
    private RecyclerView recyclerView;
    private ItemRecyclerViewAdapter itemRecyclerViewAdapter;
    private String ITEM_TYPE = "purchased_list";


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

        database = FirebaseDatabase.getInstance();
        purchasedListRef = database.getReference().child("purchased_list");;
        shoppingListRef = database.getReference().child("shopping_list");

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            purchasedList = (ArrayList<Item>) bundle.getSerializable("purchased_list");
        }
        // Initialize recycler
        recyclerView = findViewById(R.id.recyclerView);
        itemRecyclerViewAdapter = new ItemRecyclerViewAdapter(this, purchasedList, null, null, null, this, ITEM_TYPE);
        recyclerView.setAdapter(itemRecyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

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

    @Override
    public void removeFromPurchasedList(Item item) {

        for (int i = 0; i < purchasedList.size(); i++) {
            Item currItem = purchasedList.get(i);
            if (currItem.equals(item.getItemKey())) {
                purchasedList.remove(i);
                purchasedListRef.child(item.getItemKey()).removeValue();
                item.clearKey();
                shoppingListRef.push().setValue(item);
                itemRecyclerViewAdapter.notifyItemRemoved(i);
                break;
            }
        }
    }
}