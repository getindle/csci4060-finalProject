package edu.uga.cs.roommateshoppinglist;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ItemRecyclerViewAdapter extends RecyclerView.Adapter<ItemRecyclerViewAdapter.ItemHolder> {

    public static final String TAG = "ItemRecyclerViewAdapter";
    Context context;
    List<Item> shoppingList;

    public ItemRecyclerViewAdapter(Context context, List<Item> shoppingList) {
        this.context = context;
        // this.shoppingList = new ArrayList<>(shoppingList);
        this.shoppingList = shoppingList;
    }

    public void sync(Item item) {
        shoppingList.add(item);
    }

    public static class ItemHolder extends RecyclerView.ViewHolder {

        TextView cardName;
        TextView cardQuantity;
        Button editCard;
        Button addToCart;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);

            cardName = itemView.findViewById(R.id.cardName);
            cardQuantity = itemView.findViewById(R.id.cardQuantity);
            editCard = itemView.findViewById(R.id.editCard);
            addToCart = itemView.findViewById(R.id.addToCart);
        }
    }

    /*
    Inflate the layout
     */
    @NonNull
    @Override
    public ItemRecyclerViewAdapter.ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_card_layout, parent, false);

        return new ItemRecyclerViewAdapter.ItemHolder(view);
    }

    /*

     */
    @Override
    public void onBindViewHolder(@NonNull ItemRecyclerViewAdapter.ItemHolder holder, int position) {

        Item item = shoppingList.get(position);
        Log.d(TAG, "RecyclerViewAdapter.onBindViewHolder: " + item);

        holder.cardName.setText(item.getItemName());
        holder.cardQuantity.setText(String.format("%s%s", context.getString(R.string.quantity), item.getItemQuantity()));

        holder.editCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        holder.addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (context instanceof ShoppingListActivity) {
                    // call addToCart method located in ShoppingListActivity
                    ((ShoppingListActivity) context).addToCart(item);
                }
            }
        });
    }

    /*
    How many items we have in total.
     */
    @Override
    public int getItemCount() {
        return shoppingList.size();
    }
}
