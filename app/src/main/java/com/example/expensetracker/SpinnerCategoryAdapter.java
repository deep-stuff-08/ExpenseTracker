package com.example.expensetracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import java.util.ArrayList;

public class SpinnerCategoryAdapter extends ArrayAdapter<ExpenseSettings.LogoNameCombo> {

    public SpinnerCategoryAdapter(@NonNull Context context, ArrayList<ExpenseSettings.LogoNameCombo> data) {
        super(context, android.R.layout.simple_spinner_item, data);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getFilledView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getFilledView(position, convertView, parent);
    }

    private View getFilledView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.spinner_category_item, parent, false);
        }

        ExpenseSettings.LogoNameCombo item = getItem(position);
        if(item != null) {
            TextView text = convertView.findViewById(R.id.spinner_category_text);
            text.setText(item.getName());
            ImageView image = convertView.findViewById(R.id.spinner_category_image);
            image.setImageDrawable(ResourcesCompat.getDrawable(convertView.getResources(), item.getLogo(), convertView.getResources().newTheme()));
        }

        return convertView;
    }
}
