package edu.uga.cs.roommateshoppinglist;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CartDialogFragment extends DialogFragment {
    private static final String ITEM_TYPE = "cart";
    EditText totalPrice;
    private Button purchaseButton;
    private List<Item> cartList;
    ItemRecyclerViewAdapter adapter;

    public interface AddToPurchasedListListener {
        void addToPurchasedList(List<Item> items, String price);
    }

    public static CartDialogFragment newInstance(ArrayList<Item> cartList) {
        CartDialogFragment cartDialogFragment = new CartDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable("cart_list", cartList);
        cartDialogFragment.setArguments(args);
        return cartDialogFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            cartList = (ArrayList<Item>) getArguments().getSerializable("cart_list");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_cart_items, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set up recycler view in dialog and inflate
        RecyclerView recyclerView = view.findViewById(R.id.cart_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new ItemRecyclerViewAdapter(getActivity(), cartList, null, null, (ShoppingListActivity) getActivity(), null, ITEM_TYPE);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        totalPrice = view.findViewById(R.id.totalPriceEditText);
        purchaseButton = view.findViewById(R.id.purchaseButton);

        purchaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String price = totalPrice.getText().toString();
                AddToPurchasedListListener listener = (AddToPurchasedListListener) getActivity();

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference cartRef = database.getReference().child("cart_list");

                for (Item item : cartList) {
                    cartRef.child(item.getItemKey()).removeValue();
                }

                listener.addToPurchasedList(new ArrayList<>(cartList), price);
                cartList.clear();
                adapter.notifyDataSetChanged();
                dismiss();
            }
        });
    }

    public void notifyAdapter(int i) {
        adapter.notifyItemRemoved(i);
    }
}
