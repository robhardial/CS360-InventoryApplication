package com.zybooks.roberthardial_project2;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ItemAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Item> itemList;

    public ItemAdapter(Context context, ArrayList<Item> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.grid_item_layout, parent, false);
        }

        Item item = itemList.get(position);

        TextView itemNameTextView = convertView.findViewById(R.id.textViewItemName);
        TextView itemQuantityTextView = convertView.findViewById(R.id.textViewItemQuantity);
        TextView itemPriceTextView = convertView.findViewById(R.id.textViewItemPrice);

        itemNameTextView.setText(item.getItemName());
        itemQuantityTextView.setText(String.valueOf(item.getQuantity()));
        String formattedPrice = String.format("%.2f", item.getPrice()); // Format price with 2 decimal places
        itemPriceTextView.setText(formattedPrice);

        return convertView;
    }

    public void addItem(Item item) {
        itemList.add(item);
    }

    public void clear() {
        itemList.clear();
        notifyDataSetChanged();
    }

    public void addAll(ArrayList<Item> allItems) {
        itemList.addAll(allItems);
        notifyDataSetChanged();
    }

    public void deleteItem(Item itemToDelete) {
        itemList.remove(itemToDelete);
        notifyDataSetChanged();
    }
}

