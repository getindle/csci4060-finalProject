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

import java.util.List;

public class ItemRecyclerViewAdapter
        extends RecyclerView.Adapter<ItemRecyclerViewAdapter.ItemHolder> {

    public static final String TAG = "ItemRecyclerViewAdapter";

    private Context context;
    private List<Item> shoppingList;
    private AddToCartListener cartListener;
    private EditItemListener editListener;
    private RemoveFromCartListener removeListener;
    private String itemType;


    public ItemRecyclerViewAdapter () {

    }

    public ItemRecyclerViewAdapter(Context context, List<Item> shoppingList,
                                   AddToCartListener cartListener, EditItemListener editListener,
                                   RemoveFromCartListener removeListener, String itemType) {
        Log.d(TAG, "ViewType: " + itemType);

        this.context = context;
        this.shoppingList = shoppingList;
        this.cartListener = cartListener;
        this.editListener = editListener;
        this.removeListener = removeListener;
        this.itemType = itemType;
    }

    public interface AddToCartListener {
        void addToCart(Item item);
    }

    public interface EditItemListener {
        void openEditItemFragment(Item item);
    }

    public interface RemoveFromCartListener {
        void removeFromCart(Item item);
    }

//    public void sync(Item item) {
//        shoppingList.add(item);
//    }

    public static class ItemHolder extends RecyclerView.ViewHolder {

        TextView cardName;
        TextView cardQuantity;
        Button editCard;
        Button addToCart;
        Button removeButton;

        public ItemHolder(@NonNull View itemView, String itemType) {
            super(itemView);

            if (itemType.equals("shopping")) {
                cardName = itemView.findViewById(R.id.cartCardName);
                cardQuantity = itemView.findViewById(R.id.cartCardQuantity);
                editCard = itemView.findViewById(R.id.editCard);
                addToCart = itemView.findViewById(R.id.removeButton);
            }
            else {
                cardName = itemView.findViewById(R.id.cartCardName);
                cardQuantity = itemView.findViewById(R.id.cartCardQuantity);
                removeButton = itemView.findViewById(R.id.removeButton);
            }
        }
    }

    /*
    Inflate the layout
     */
    @NonNull
    @Override
    public ItemRecyclerViewAdapter.ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        View view;
        if (itemType.equals("shopping"))
        {
            view = inflater.inflate(R.layout.item_card_layout, parent, false);
        }
        else {
            view = inflater.inflate(R.layout.cart_card_layout, parent, false);
        }

        return new ItemRecyclerViewAdapter.ItemHolder(view, itemType);
    }

    /*

     */
    @Override
    public void onBindViewHolder(@NonNull ItemRecyclerViewAdapter.ItemHolder holder, int position) {

        Item item = shoppingList.get(position);

        Log.d(TAG, "RecyclerViewAdapter.onBindViewHolder: " + item);

        holder.cardName.setText(item.getItemName());
        holder.cardQuantity.setText(String.format("%s%s", context.getString(R.string.quantity), item.getItemQuantity()));

        if (itemType.equals("shopping")) {

            holder.editCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    editListener.openEditItemFragment(item);
                }
            });

            holder.addToCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cartListener.addToCart(item);
                }
            });
        }
        else {
            holder.removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    removeListener.removeFromCart(item);
                }
            });

        }
    }

    /*
    How many items we have in total.
     */
    @Override
    public int getItemCount() {
        return shoppingList.size();
    }

}
