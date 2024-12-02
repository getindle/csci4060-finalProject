package edu.uga.cs.roommateshoppinglist;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditItemDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditItemDialogFragment extends DialogFragment {

    public static final String TAG = "EditItemDialogFragment";

    private EditText itemName;
    private EditText itemQuantity;
    private Button deleteButton;
    private Button saveButton;

    private Item item;

    public EditItemDialogFragment() {
        // Required empty public constructor
    }

    public interface UpdateItemListener {
        void updateItem(Item item);
    }

    public interface RemoveItemListener {
        void removeFromShoppingList(Item item);
    }

    public static EditItemDialogFragment newInstance(Item item) {
        EditItemDialogFragment fragment = new EditItemDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable("item", item);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            item = (Item) getArguments().getSerializable("item");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.edit_item_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        itemName = getView().findViewById(R.id.editItemName);
        itemQuantity = getView().findViewById(R.id.editItemQuantity);
        deleteButton = getView().findViewById(R.id.deleteButton);
        saveButton = getView().findViewById(R.id.saveButton);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RemoveItemListener listener = (RemoveItemListener) getActivity();
                listener.removeFromShoppingList(item);
                dismiss();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                item.setItemName(itemName.getText().toString());
                item.setItemQuantity(itemQuantity.getText().toString());
                UpdateItemListener listener = (UpdateItemListener) getActivity();
                listener.updateItem(item);
                dismiss();
            }
        });
    }
}