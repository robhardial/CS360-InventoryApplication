package com.zybooks.roberthardial_project2;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

public class DataDisplayActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    private GridView gridView;
    private Button buttonAddData;
    private EditText editTextDataChange;
    private LinearLayout rowLayout;
    private Button buttonDeleteData;
    private Button buttonEditData;

    private  Button buttonLogout;

    private DatabaseHelper databaseHelper;
    private ItemAdapter itemAdapter;

    private int userId;


    private SparseBooleanArray selectedItems = new SparseBooleanArray();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_display);

        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getInt("user_id", -1);

        boolean smsPermissionGranted = sharedPreferences.getBoolean("sms_permission_granted_" + userId, false);

        databaseHelper = new DatabaseHelper(this);
        gridView = findViewById(R.id.InventoryTracker);
        buttonAddData = findViewById(R.id.buttonAddData);
        rowLayout = findViewById(R.id.rowLayout);
        buttonDeleteData = findViewById(R.id.buttonDeleteData);
        buttonEditData = findViewById(R.id.buttonEditData);
        buttonLogout = findViewById(R.id.buttonLogout);

        ArrayList<Item> itemList = databaseHelper.getAllItems(userId);
        itemAdapter = new ItemAdapter(this, itemList);
        gridView.setAdapter(itemAdapter);
        gridView.setOnItemClickListener(this);

        // Use the permission response to enable/disable SMS alert feature
        if (smsPermissionGranted) {
            // Enable SMS alert feature=
            for (Item item : itemList) {
                if (item.getQuantity() < 5) {
                    sendSMSAlert(item);
                }
            }
        }



        // Set click listeners for buttons
        buttonAddData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle adding data logic here
                openAddDataDialog();
            }
        });


        buttonDeleteData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteSelectedItem();
            }
        });

        buttonEditData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editSelectedItem();
            }
        });

        // Set click listener for GridView items
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Clear the selection status of all items
                selectedItems.clear();
                for (int i = 0; i < gridView.getCount(); i++) {
                    gridView.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
                }

                // Change the selection status of the clicked item
                boolean isSelected = !selectedItems.get(position, false);
                selectedItems.put(position, isSelected);

                // Update the view to show the selection status
                if (isSelected) {
                    view.setBackgroundColor(Color.parseColor("#FFC107")); // Use your desired color here
                } else {
                    view.setBackgroundColor(Color.TRANSPARENT);
                }

                // Notify the adapter that the data has changed
                itemAdapter.notifyDataSetChanged();
            }
        });

        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Start LoginActivity to return to the login screen
                Intent intent = new Intent(DataDisplayActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // Close the current activity
            }
        });

    }

    private void openAddDataDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit, null);
        dialogBuilder.setView(dialogView);

        final EditText editTextName = dialogView.findViewById(R.id.editTextName);
        final EditText editTextQuantity = dialogView.findViewById(R.id.editTextQuantity);
        final EditText editTextPrice = dialogView.findViewById(R.id.editTextPrice);

        dialogBuilder.setTitle("Add New Item");
        dialogBuilder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String itemName = editTextName.getText().toString();
                int quantity = Integer.parseInt(editTextQuantity.getText().toString());
                double price = Double.parseDouble(editTextPrice.getText().toString());

                // Retrieve user_id from shared preferences
                SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                int userId = sharedPreferences.getInt("user_id", -1);

                if (userId != -1) {
                    // Add the new item to the database with the user ID
                    long newRowId = databaseHelper.addItem(itemName, quantity, price, userId);

                    if (newRowId != -1) {
                        // Update the GridView adapter with the new data
                        itemAdapter.addItem(new Item((int) newRowId, itemName, quantity, price));
                        itemAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(DataDisplayActivity.this, "Error adding item", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        dialogBuilder.setNegativeButton("Cancel", null);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // Handle item click here
        selectedItems.put(position, !selectedItems.get(position));
        itemAdapter.notifyDataSetChanged();
    }


    private void deleteSelectedItem() {

        for (int i = selectedItems.size() - 1; i >= 0; i--) {
            if (selectedItems.valueAt(i)) {
                int position = selectedItems.keyAt(i);
                Item itemToDelete = (Item) itemAdapter.getItem(position);
                itemAdapter.deleteItem(itemToDelete);
            }
        }

        // Clear the checked items
        gridView.clearChoices();
    }



    private void refreshGridView() {
        itemAdapter.clear();
        itemAdapter.addAll(databaseHelper.getAllItems(userId));
        gridView.clearChoices();
        itemAdapter.notifyDataSetChanged();
    }

    private void editSelectedItem() {
        int selectedItemPosition = getSelectedItemPosition();

        if (selectedItemPosition != -1) {
            // Get the selected item from the adapter
            Item selectedItem = (Item) itemAdapter.getItem(selectedItemPosition);

            // Open the edit dialog with the selected item's details
            openEditDataDialog(selectedItem);
        } else {
            Toast.makeText(this, "Please select an item to edit", Toast.LENGTH_SHORT).show();
        }
    }

    private int getSelectedItemPosition() {
        for (int i = 0; i < selectedItems.size(); i++) {
            int key = selectedItems.keyAt(i);
            if (selectedItems.get(key)) {
                return key;
            }
        }
        return -1; // No item is selected
    }

    private void openEditDataDialog(Item itemToEdit) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit, null);
        dialogBuilder.setView(dialogView);

        final EditText editTextName = dialogView.findViewById(R.id.editTextName);
        final EditText editTextQuantity = dialogView.findViewById(R.id.editTextQuantity);
        final EditText editTextPrice = dialogView.findViewById(R.id.editTextPrice);

        // Set the current item's details in the dialog's EditText fields
        editTextName.setText(itemToEdit.getItemName());
        editTextQuantity.setText(String.valueOf(itemToEdit.getQuantity()));
        editTextPrice.setText(String.valueOf(itemToEdit.getPrice()));

        dialogBuilder.setTitle("Edit Item");
        dialogBuilder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Update the item's details in the database and adapter
                String newName = editTextName.getText().toString();
                int newQuantity = Integer.parseInt(editTextQuantity.getText().toString());
                double newPrice = Double.parseDouble(editTextPrice.getText().toString());

                itemToEdit.setItemName(newName);
                itemToEdit.setQuantity(newQuantity);
                itemToEdit.setPrice(newPrice);

                // Update the GridView adapter with the edited data
                itemAdapter.notifyDataSetChanged();
            }
        });
        dialogBuilder.setNegativeButton("Cancel", null);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    private void sendSMSAlert(Item item) {
        String phoneNumber = "15551234567"; // Replace with the actual phone number
        String message = "Alert: Item " + item.getItemName() + " has low quantity (" + item.getQuantity() + ")";

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(this, "SMS alert sent for " + item.getItemName(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Failed to send SMS alert for " + item.getItemName(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

}

