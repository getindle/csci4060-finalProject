package edu.uga.cs.roommateshoppinglist;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.reflect.Array;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ShoppingListActivity extends AppCompatActivity
        implements AddItemDialogFragment.AddItemDialogListener,
        ItemRecyclerViewAdapter.AddToCartListener,
        ItemRecyclerViewAdapter.EditItemListener,
        ItemRecyclerViewAdapter.RemoveFromCartListener,
        EditItemDialogFragment.UpdateItemListener,
        EditItemDialogFragment.RemoveItemListener,
        CartDialogFragment.AddToPurchasedListListener {

    public static final String TAG = "ShoppingListActivity";

    // instance variables to set up hamburger icon
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;

    private List<Item> shoppingList;
    private ArrayList<Item> cartList;
    private ArrayList<Item> purchasedList;
    private RecyclerView recyclerView;
    private ItemRecyclerViewAdapter itemRecyclerViewAdapter;
    public static final String ITEM_TYPE = "shopping";


    private FirebaseDatabase database;
    private DatabaseReference rootRef;
    private DatabaseReference shoppingListRef;
    private DatabaseReference cartListRef;
    private DatabaseReference purchasedListRef;

    private CartDialogFragment cartDialogFragment;

    String totalCartPrice;
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

        cartList = new ArrayList<>();
        purchasedList = new ArrayList<>();


        // Initialize recycler
        recyclerView = findViewById(R.id.recyclerView);
        shoppingList = new ArrayList<>();
        itemRecyclerViewAdapter = new ItemRecyclerViewAdapter(this, shoppingList, this, this, null, null, ITEM_TYPE);
        recyclerView.setAdapter(itemRecyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize database
        database = FirebaseDatabase.getInstance();
        rootRef = database.getReference();
        shoppingListRef = rootRef.child("shopping_list");
        cartListRef = rootRef.child("cart_list");
        purchasedListRef = rootRef.child("purchased_list");

        setUpChildEventListeners();

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
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("purchased_list", purchasedList);
                    bundle.putString("total_price", totalCartPrice);
                    Intent intent = new Intent(this, PurchasedListActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    break;
                case 1: // BUTTON 2
                    // [BUTTON 2 LOGIC GOES HERE]
                    break;
                case 2: // HELP OPTION
                    // [LOGOUT LOGIC GOES HERE]
                    Intent loginIntent = new Intent(this, LoginActivity.class);
                    startActivity(loginIntent);
                    break;
            }
            drawerLayout.closeDrawers(); // Close the drawer
        });

        // set up floating action button
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new ShoppingListActivity.FloatingActionButtonClickListener());
    }

    public void setUpChildEventListeners() {

        shoppingListRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d(TAG, "onChildAdded: " + snapshot.getKey());

                Item newItem = snapshot.getValue(Item.class);
                newItem.setItemKey(snapshot.getKey());
                shoppingList.add(newItem);
                itemRecyclerViewAdapter.notifyItemInserted(shoppingList.size() - 1);
                recyclerView.smoothScrollToPosition(shoppingList.size() - 1);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d(TAG, "onChildChanged: " + snapshot.getKey());

                Item updatedItem = snapshot.getValue(Item.class);

                for (Item item : shoppingList) {
                    if (item.equals(updatedItem.getItemKey())) {
                        if (!Objects.equals(item.getItemName(), updatedItem.getItemName())) {
                            item.setItemName(updatedItem.getItemName());
                        }
                        if (!Objects.equals(item.getItemQuantity(), updatedItem.getItemQuantity())) {
                            item.setItemQuantity(updatedItem.getItemQuantity());
                        }
                    }
                }

                itemRecyclerViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "onChildRemoved: " + snapshot.getKey());

                for (int i = 0; i < shoppingList.size(); i++) {
                    if (shoppingList.get(i).equals(snapshot.getKey())) {
                        shoppingList.remove(i);
                        itemRecyclerViewAdapter.notifyItemRemoved(i);
                        break;
                    }
                }

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        cartListRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                Item cartItem = snapshot.getValue(Item.class);
                cartItem.setItemKey(snapshot.getKey());
                cartList.add(cartItem);

                /*
                An item that is added to the cart has its key cleared. This is how
                we search for it in order to delete it from the shopping list.
                 */
                for (int i = 0; i < shoppingList.size(); i++) {
                    if (shoppingList.get(i).getItemKey() == null) {
                        shoppingList.remove(i);
                        itemRecyclerViewAdapter.notifyItemRemoved(i);
                        break;
                    }
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                // TODO: update the cart to reflect the removed item


                for (int i = 0; i < cartList.size(); i++) {
                    if (cartList.get(i).equals(snapshot.getKey())) {
                        Log.d(TAG, "itemMatched");
                        cartList.remove(i);
                        cartDialogFragment.notifyAdapter(i);
                        break;
                    }
                }

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        purchasedListRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d(TAG, "onChildAdded: " + snapshot.getKey());

                Item newItem = snapshot.getValue(Item.class);
                newItem.setItemKey(snapshot.getKey());
                purchasedList.add(newItem);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                Log.d(TAG, "onChildRemoved: " + snapshot.getKey());

                for (int i = 0; i < purchasedList.size(); i++) {
                    if (purchasedList.get(i).equals(snapshot.getKey())) {
                        purchasedList.remove(i);
                        itemRecyclerViewAdapter.notifyItemRemoved(i);
                        break;
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    /*
    Opens a dialog fragment to add a new item to the shopping list.
     */
    private class FloatingActionButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            AddItemDialogFragment dialogFragment = new AddItemDialogFragment();
            dialogFragment.show(getSupportFragmentManager(), "AddItemFragment");
        }
    }

    /*
    Update the shopping list with the new item and save item in Firebase.
     */
    public void saveNewItem(Item item) {
        Log.d(TAG, "ShoppingListActivity.saveNewItem");

        // Add a new item to the shopping list in the database
        shoppingListRef.push().setValue(item).addOnSuccessListener(
                unused -> Log.d(TAG, "Item added successfully")
        );
    }

    public void removeFromShoppingList(Item item) {
        shoppingListRef.child(item.getItemKey()).removeValue().addOnSuccessListener(
                unused -> Log.d(TAG, "removeFromShoppingList: Item removed")
        );
    }

    /*
    Open edit dialog fragment.
     */
    public void openEditItemFragment(Item item) {
        EditItemDialogFragment editItemDialogFragment = EditItemDialogFragment.newInstance(item);
        editItemDialogFragment.show(getSupportFragmentManager(), "edit_item");
    }

    public void updateItem(Item item) {
        Map<String, Object> update = new HashMap<>();
        update.put("itemName", item.getItemName());
        update.put("itemQuantity", item.getItemQuantity());

        shoppingListRef.child(item.getItemKey()).updateChildren(update);
    }

    /*
    Update the shopping cart and save to Firebase.
     */
    public void addToCart(Item item) {

        // Add a new item to the cart list after removing from shopping list
        shoppingListRef.child(item.getItemKey()).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "addToCart: Item deleted from shopping list");

                item.clearKey();

                cartListRef.push().setValue(item).addOnSuccessListener(
                        unused -> Log.d(TAG, "addToCart: Item added to cart list")
                );

            } else {
                Log.e(TAG, "Update failed", task.getException());
            }
        });

    }

    public void removeFromCart(Item item) {
        cartListRef.child(item.getItemKey()).removeValue().addOnSuccessListener(
                unused -> Log.d(TAG, "removeFromCart")
        );

        shoppingListRef.push().setValue(item);
    }

    public void addToPurchasedList(List<Item> items, String totalPrice) {
        totalCartPrice = totalPrice;
        for (Item item : items) {
            item.clearKey();
            purchasedListRef.push().setValue(item);
        }
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

    private void showCartDialogAlert() {
        cartDialogFragment = CartDialogFragment.newInstance(cartList);
        cartDialogFragment.show(getSupportFragmentManager(), "cartDialog");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");

    }
}