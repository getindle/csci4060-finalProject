package edu.uga.cs.roommateshoppinglist;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
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

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class PurchasedListActivity extends AppCompatActivity implements ItemRecyclerViewAdapter.PurchasedListListener {

    public static final String TAG = "PurchasedListActivity";

    private DatabaseReference purchasedListRef;
    private DatabaseReference shoppingListRef;
    private FirebaseDatabase database;
    private ArrayList<Item> purchasedList;
    private ArrayList<Item> removeList;
    private RecyclerView recyclerView;
    private ItemRecyclerViewAdapter itemRecyclerViewAdapter;
    private String ITEM_TYPE = "purchased_list";

    private String totalPrice;

    private TextView priceTv;
    private Button editButton;


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
        purchasedListRef = database.getReference().child("purchased_list");
        shoppingListRef = database.getReference().child("shopping_list");

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            totalPrice = bundle.getString("total_price");
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

        editButton = findViewById(R.id.editButton);
        editButton.setOnClickListener(new ButtonOnClickListener());

        priceTv = findViewById(R.id.priceTv);

        try {
            double price = Double.parseDouble(totalPrice);
            price = price * 1.07;
            priceTv.setText(String.format("%.2f", price, " (w/ Tax)")); // update textview
        } catch (NullPointerException npe) {
            priceTv.setText("$0.00");
        } catch (NumberFormatException nfe) {
            priceTv.setText("0.00");
        }

        // priceTv.setText("$" + totalPrice);
    }

    private class ButtonOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            EditText input = new EditText(view.getContext());
            // input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            input.setHint("Enter New Price Here");

            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setTitle("Total Price");
            builder.setView(input);
            builder.setPositiveButton("OK", (dialog, which) -> {
                String newPrice = input.getText().toString();
                if (!newPrice.isEmpty()) { // must enter a price
                    try {
                        double totalPrice = Double.parseDouble(newPrice);
                        priceTv.setText("$" + String.format("%.2f", totalPrice)); // update textview
                    } catch (NumberFormatException e) { // must enter numbers
                        Toast.makeText(view.getContext(), "Invalid price.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(view.getContext(), "Must Enter a Price.", Toast.LENGTH_SHORT).show();
                }
            });
            builder.show(); // present alert dialog
        }
    }

    /*
    Opens a dialog fragment to add a new item to the shopping list.
     */
    private class FloatingSettleButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            for (int i = 0; i < purchasedList.size(); i++) {
                Item item = purchasedList.get(i);
                if (item.getItemKey().equals(item.getItemKey())) {
                    purchasedList.remove(i);

                    purchasedListRef.child(item.getItemKey()).removeValue().addOnSuccessListener(
                            unused -> Log.d(TAG, "Item removed from purchased cart")
                    );

                    itemRecyclerViewAdapter.notifyItemRemoved(i);
                }
            }

            TextView total = new TextView(view.getContext());
            total.setText("Total Price: " + priceTv.getText().toString());

            double userCost = 0.0;
            double userAvgCost = 0.0;
            String newPrice = priceTv.getText().toString().replace("$", "");

            if (!newPrice.isEmpty()) {
                try {
                    double totalPrice = Double.parseDouble(newPrice);
                    userCost = totalPrice / 2;
                    userAvgCost = totalPrice / 2 / MainActivity.numOfPurchases;
                } catch (NumberFormatException e) {
                    Toast.makeText(view.getContext(), "Invalid price.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(view.getContext(), "Must Enter a Price.", Toast.LENGTH_SHORT).show();
            }

            TextView user1Cost = new TextView(view.getContext());
            user1Cost.setText("George's Cost: " + "$" + String.format("%.2f", userCost));

            TextView user2Cost = new TextView(view.getContext());
            user2Cost.setText("Santos's Cost: " + "$" + String.format("%.2f", userCost));

            TextView user1AvgCost = new TextView(view.getContext());
            user1AvgCost.setText("George's Average Cost Per Purchase: " + "$" + String.format("%.2f", userAvgCost));

            TextView user2AvgCost = new TextView(view.getContext());
            user2AvgCost.setText("Santos's Average Cost Per Purchase: " + "$" + String.format("%.2f", userAvgCost));

            // add views in a vertical layout (row-by-row)
            LinearLayout layout = new LinearLayout(view.getContext());
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.addView(total);
            layout.addView(user1Cost);
            layout.addView(user2Cost);
            layout.addView(user1AvgCost);
            layout.addView(user2AvgCost);

            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setTitle("Costs Are Settled!");
            builder.setView(layout);
            builder.setPositiveButton("OK", (dialog, which) -> {

            });
            // reset purchase #
            MainActivity.numOfPurchases = 0;

            // display alert dialog
            builder.show();
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
            if (purchasedList.get(i).getItemKey().equals(item.getItemKey())) {
                purchasedList.remove(i);

                purchasedListRef.child(item.getItemKey()).removeValue().addOnSuccessListener(
                        unused -> Log.d(TAG, "Item removed from purchased cart")
                );;

                item.clearKey();

                shoppingListRef.push().setValue(item).addOnSuccessListener(
                        unused -> Log.d(TAG, "Item added to shopping list")
                );

                itemRecyclerViewAdapter.notifyItemRemoved(i);
                break;
            }
        }
    }
}