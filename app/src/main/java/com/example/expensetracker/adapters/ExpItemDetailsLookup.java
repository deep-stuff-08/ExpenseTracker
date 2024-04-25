package com.example.expensetracker.adapters;

import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.widget.RecyclerView;

public class ExpItemDetailsLookup extends ItemDetailsLookup<Long> {
    RecyclerView recyclerView;

    public ExpItemDetailsLookup(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    @Nullable
    @Override
    public ItemDetails<Long> getItemDetails(@NonNull MotionEvent e) {
        View view = recyclerView.findChildViewUnder(e.getX(), e.getY());
        if (view != null) {
            return ((ExpEntryAdapter.ViewHolder)recyclerView.getChildViewHolder(view)).getItemDetails();
        }
        return null;
    }
}
