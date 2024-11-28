package edu.uga.cs.roommateshoppinglist;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CartDialogFragment extends DialogFragment {
    private List<Item> cartItems;

    // Constructor to pass the cart items to the dialog
    public CartDialogFragment(List<Item> cartItems) {
        this.cartItems = cartItems;
    }

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // create alert dialog and inflate the view
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_cart_items, null);

        // set up recycler view in dialog and inflate
        RecyclerView recyclerView = dialogView.findViewById(R.id.cart_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        ItemRecyclerViewAdapter adapter = new ItemRecyclerViewAdapter(getActivity(), cartItems);
        recyclerView.setAdapter(adapter);

        builder.setView(dialogView)
                .setTitle("Cart Items")
                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dismiss();
                    }
                })
                .setNegativeButton("Clear Cart", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        cartItems.clear();
                        adapter.notifyDataSetChanged(); // update cart recycler view as cleared
                    }
                });
        // THIS IS WHERE MAKE PURCHASE BUTTON CAN BE ADDED TO CART/BASKET DIALOG ALERT

        return builder.create();
    }
}
