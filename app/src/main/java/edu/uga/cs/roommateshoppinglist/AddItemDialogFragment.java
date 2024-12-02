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
 * Use the {@link AddItemDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddItemDialogFragment extends DialogFragment {

    private static final String TAG = "AddItemFragment";

    private EditText itemName;
    private EditText itemQuantity;
    private Button saveButton;

    public interface AddItemDialogListener {
        void saveNewItem(Item item);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_item, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        itemName = getView().findViewById(R.id.editItemName);
        itemQuantity = getView().findViewById(R.id.editItemQuantity);
        saveButton = getView().findViewById(R.id.saveButton);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get user input
                String name = itemName.getText().toString();
                String quantity = itemQuantity.getText().toString();

                // Add the new item
                AddItemDialogListener listener = (AddItemDialogListener) getActivity();
                listener.saveNewItem(new Item(name, quantity));

                // Close the dialog fragment
                dismiss();
            }
        });

    }

}