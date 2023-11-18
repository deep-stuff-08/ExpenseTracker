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

public class SpinnerCategoryAdapter extends ArrayAdapter<SpinnerCategoryAdapter.CategoryItem> {

    public SpinnerCategoryAdapter(@NonNull Context context, CategoryItem[] data) {
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

        CategoryItem item = getItem(position);
        if(item != null) {
            TextView text = convertView.findViewById(R.id.spinner_category_text);
            text.setText(item.getText());
            ImageView image = convertView.findViewById(R.id.spinner_category_image);
            image.setImageDrawable(ResourcesCompat.getDrawable(convertView.getResources(), item.getId(), convertView.getResources().newTheme()));
        }

        return convertView;
    }

    public static class CategoryItem {
        private String text;
        private int id;

        CategoryItem(String text, int id) {
            this.text = text;
            this.id = id;
        }

        @NonNull
        public String getText() {
            return text;
        }

        public int getId() {
            return id;
        }
    }
}
