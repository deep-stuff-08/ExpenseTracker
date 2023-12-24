package com.example.expensetracker;

import static com.example.expensetracker.POJO.SettingsType.CATEGORY;
import static com.example.expensetracker.POJO.SettingsType.PAYMENT;
import static com.example.expensetracker.POJO.SettingsType.SUBCATEGORY;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensetracker.POJO.Category;
import com.example.expensetracker.POJO.PaymentType;
import com.example.expensetracker.POJO.SettingsParent;
import com.example.expensetracker.POJO.SettingsType;
import com.example.expensetracker.POJO.SubCategory;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.Set;

public class SettingsModifyAdapter extends RecyclerView.Adapter<SettingsModifyAdapter.SettingsModifyViewHolder> {
    private final ArrayList<SettingsParent> settingsData;

    private SettingsUpdateListener updateListener;

    public SettingsModifyAdapter(ArrayList<? extends SettingsParent> data, SettingsUpdateListener listener) {
        settingsData = (ArrayList<SettingsParent>) data;
        updateListener = listener;
    }
    @NonNull
    @Override
    public SettingsModifyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.modify_methods, parent, false);
        return new SettingsModifyAdapter.SettingsModifyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SettingsModifyViewHolder holder, int position) {
        SettingsParent item = settingsData.get(position);
        int bgRes = R.color.grey;
        int imageRes = 0;
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
        holder.textview.setText(item.getName());
        holder.imageView.setImageResource(imageRes);
        holder.imageView.setBackgroundColor(holder.itemView.getResources().getColor(bgRes, holder.itemView.getContext().getTheme()));
        holder.modifyBtn.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            View diagview = View.inflate(view.getContext(), R.layout.edittext_dialog, null);
            builder.setView(diagview);
            builder.setTitle("Modify");
            builder.setPositiveButton("Ok", (dialogInterface, i) -> {
                EditText editText = diagview.findViewById(R.id.dialog_edittext);
                String t = "";
                String newName = editText.getText().toString();
                switch (item.getType()) {
                    case PAYMENT:
                        t = ((PaymentType)item).getName();
                        break;
                    case CATEGORY:
                        t = ((Category)item).getName();
                        break;
                    case SUBCATEGORY:
                        t = ((SubCategory)item).getName();
                        break;
                }
                if(!newName.equals(t)) {
                    updateListener.onSettingsNameUpdateListener(holder.getAdapterPosition(), newName);
                    updateListener.onSettingsLogoUpdateListener(holder.getAdapterPosition(), R.drawable.ic_launcher_foreground);
                }
            });
            builder.setNegativeButton("Cancel", null);
            AlertDialog dialog = builder.create();
            EditText editText = diagview.findViewById(R.id.dialog_edittext);
            editText.setText(item.getName());
            dialog.show();
        });
        holder.removeBtn.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setTitle("Confirm Delete");
            builder.setMessage("Are you sure you want to delete '"+settingsData.get(holder.getAdapterPosition()).getName()+"'?");
            builder.setPositiveButton("Yes", (dialogInterface, i) -> {
                updateListener.onSettingsDeleteListener(holder.getAdapterPosition());
            });
            builder.setNegativeButton("No", null);
            builder.show();
        });
    }

    @Override
    public int getItemCount() {
        return settingsData.size();
    }

    public static class SettingsModifyViewHolder extends  RecyclerView.ViewHolder {
        private final TextView textview;
        private final ShapeableImageView imageView;
        private final ImageButton modifyBtn;
        private final ImageButton removeBtn;
        public SettingsModifyViewHolder(@NonNull View itemView) {
            super(itemView);
            textview = itemView.findViewById(R.id.modify_text);
            imageView = itemView.findViewById(R.id.modify_image);
            modifyBtn = itemView.findViewById(R.id.modify_modify_btn);
            removeBtn = itemView.findViewById(R.id.modify_remove_btn);
        }
    }
}
