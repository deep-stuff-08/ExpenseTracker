package com.example.expensetracker.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.expensetracker.pojo.Category;
import com.example.expensetracker.pojo.PaymentType;
import com.example.expensetracker.pojo.SettingsParent;
import com.example.expensetracker.pojo.SubCategory;
import com.example.expensetracker.R;

import java.util.ArrayList;
import java.util.List;

public class ComboBoxAdapter extends ArrayAdapter<SettingsParent> {

    int backgroundColorId;

    @SuppressWarnings("unchecked")
    public ComboBoxAdapter(@NonNull Context context, ArrayList<? extends SettingsParent> data, int backgroundColorId) {
        super(context, android.R.layout.simple_spinner_item, (List<SettingsParent>) data);
        this.backgroundColorId = backgroundColorId;
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

    public void setBackgroundColorId(int bgId) {
        backgroundColorId = bgId;
    }

    private View getFilledView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.adapter_combobox, parent, false);
        }

        SettingsParent item = getItem(position);
        if(item != null) {
            TextView text = convertView.findViewById(R.id.spinner_category_text);
            ImageView image = convertView.findViewById(R.id.spinner_category_image);
            int imageRes = 0;
            int bgRes = backgroundColorId;
            switch (item.getType()) {
                case PAYMENT:
                    imageRes = ((PaymentType)item).getDrawableId();
                    break;
                case CATEGORY:
                    bgRes = ((Category)item).getColorId();
                    break;
                case SUBCATEGORY:
                    imageRes = ((SubCategory)item).getDrawableId();
                    break;
            }
            text.setText(item.getName());
            image.setImageResource(imageRes);
            image.setBackgroundColor(convertView.getResources().getColor(bgRes, convertView.getContext().getTheme()));
        }

        return convertView;
    }
}
