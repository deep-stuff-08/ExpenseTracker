package com.example.expensetracker.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class TransferUserAdapter extends ArrayAdapter<String> {
    public TransferUserAdapter(@NonNull Context context, @NonNull ArrayList<String> objects) {
        super(context, android.R.layout.simple_spinner_item, new ArrayList<>(objects));
        insert("Select an User", 0);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = super.getDropDownView(position, convertView, parent);
        if(position == 0) {
            v.setVisibility(View.GONE);
        }
        return v;
    }
}
