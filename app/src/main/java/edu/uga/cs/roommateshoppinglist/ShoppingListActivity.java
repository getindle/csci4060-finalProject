package edu.uga.cs.roommateshoppinglist;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ShoppingListActivity extends AppCompatActivity implements AddItemDialogFragment.AddItemDialogListener {

    public static final String TAG = "ShoppingListActivity";

    // instance variables to set up hamburger icon
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;

    private List<Item> shoppingList;
    RecyclerView recyclerView;
    ItemRecyclerViewAdapter itemRecyclerViewAdapter;

    private List<Item> cartItems;

    private FirebaseDatabase database;
    private DatabaseReference myRef;

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

        cartItems = new ArrayList<>();

        // initialize recycler
        recyclerView = findViewById(R.id.recyclerView);
        shoppingList = new ArrayList<>();
        itemRecyclerViewAdapter = new ItemRecyclerViewAdapter(this, shoppingList);
        recyclerView.setAdapter(itemRecyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize database
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        // Set up a listener that listens for changes in Firebase
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
               Log.d(TAG, "onChildAdded: " + snapshot.getKey());

                Item newItem = snapshot.getValue(Item.class);
                newItem.setItemKey(snapshot.getKey());
                shoppingList.add(newItem);
                // itemRecyclerViewAdapter.sync(newItem);
                itemRecyclerViewAdapter.notifyItemInserted(shoppingList.size() - 1);
                recyclerView.smoothScrollToPosition(shoppingList.size() - 1);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d(TAG, "onChildChanged: " + snapshot.getKey());
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "onChildRemoved: " + snapshot.getKey());

                Item deletedItem = snapshot.getValue(Item.class);

                int deletedItemIndex = 0;
                for (int i = 0; i < shoppingList.size(); i++) {
                    if (shoppingList.get(i).equals(snapshot.getKey())) {
                        deletedItemIndex = i;
                        shoppingList.remove(i);
                        break;
                    }
                }
                itemRecyclerViewAdapter.notifyItemRemoved(deletedItemIndex);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        myRef.child("shopping_list").addChildEventListener(childEventListener);

        // create drawer
        drawerLayout = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState(); // sync state so "hamburger icon" shows instead of "back arrow"

        // inflate list with button options
        String[] drawerItems = {
                "Recently Purchased",
                "Help",
                "Logout"};

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
        fab.setOnClickListener(new ShoppingListActivity.FloatingActionButtonClickListener());
    }


    /*
    Opens a dialog fragment to add a new item to the shopping list.
     */
    private class FloatingActionButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            AddItemDialogFragment dialogFragment = new AddItemDialogFragment();
            dialogFragment.setShoppingListActivity(ShoppingListActivity.this);
            dialogFragment.show(getSupportFragmentManager(), "AddItemFragment");

        }
    }

    /*
    Update the shopping list with the new item by displaying to user and saving item in Firebase.
     */
    public void saveNewItem(Item item) {
        Log.d(TAG, "ShoppingListActivity.saveNewItem");

//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference itemRef = myRef.child("items");
//        DatabaseReference newItemRef = itemRef.push();
//        myRef.push().setValue(item);

        // Add a new item to the shopping list in the database
        DatabaseReference newRef = myRef.child("shopping_list").push();
        newRef.setValue(item).addOnSuccessListener(unused -> Log.d(TAG, "Item added successfully"));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }

        if (item.getItemId() == R.id.action_cart) {
            showCartDialogAlert(); // newly created method to show basket contents
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cart, menu); // inflate cart picture in action bar
        return true;
    }

    /**
     * Adds an item from the shopping list into the cart (basket).
     *
     * @param item the item to be added
     */
    public void addToCart(Item item) {
        if (!cartItems.contains(item)) {
            cartItems.add(item); // add item to cart/basket
        } else {
            Log.d(TAG, "Item already in cart: " + item.getItemName());
        }
    }

    private void showCartDialogAlert() {
        CartDialogFragment cartDialogFragment = new CartDialogFragment(cartItems);
        cartDialogFragment.show(getSupportFragmentManager(), "cartDialog");

    }
}