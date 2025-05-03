package com.example.expensetracker.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.provider.DocumentsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensetracker.R;
import com.google.android.material.snackbar.Snackbar;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ExportEntryAdapter extends RecyclerView.Adapter<ExportEntryAdapter.ViewHolder> {
    private final ArrayList<String> dataList;
    private final ArrayList<ActivityResultLauncher<Intent>> activityLauncher;
    public ExportEntryAdapter(ArrayList<ActivityResultLauncher<Intent>> activityResultLauncher) {
        dataList = new ArrayList<>();
        dataList.add("Export Data");
        dataList.add("Export Database");
        dataList.add("Import Database");
        dataList.add("Export Settings");
        dataList.add("Import Settings");
        activityLauncher = activityResultLauncher;
    }

    @NonNull
    @Override
    public ExportEntryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TextView textView = new TextView(parent.getContext());
        textView.setTextSize(24);
        textView.setPadding(40, 20, 40, 20);
        return new ExportEntryAdapter.ViewHolder(textView);
    }
    @Override
    public void onBindViewHolder(@NonNull ExportEntryAdapter.ViewHolder holder, int position) {
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent();
            switch (position) {
                case 0: {
                    intent.setAction(Intent.ACTION_CREATE_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("text/csv");
                    intent.putExtra(Intent.EXTRA_TITLE, "data.csv");
                    break;
                }
                case 1:
                    intent.setAction(Intent.ACTION_CREATE_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("application/octet-stream");
                    intent.putExtra(Intent.EXTRA_TITLE, "database.db");
                    break;
                case 3: {
                    intent.setAction(Intent.ACTION_CREATE_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("application/json");
                    intent.putExtra(Intent.EXTRA_TITLE, "settings.json");
                    break;
                }
                case 4: {
                    intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("*/*");
                    break;
                }
                default:
                    throw new RuntimeException("Not Possible");
            }
            activityLauncher.get(position).launch(intent);
        });
        holder.textview.setText(dataList.get(position));
    }
    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textview;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textview = (TextView) itemView;
        }
    }
}
