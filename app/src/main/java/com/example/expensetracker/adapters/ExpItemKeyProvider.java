package com.example.expensetracker.adapters;

import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.selection.ItemKeyProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensetracker.pojo.Entry;

import java.util.ArrayList;

public class ExpItemKeyProvider extends ItemKeyProvider<Long> {
    ArrayList<Entry> entries;

    public ExpItemKeyProvider(ArrayList<Entry> entries) {
        super(SCOPE_CACHED);
        this.entries = entries;
    }

    @Nullable
    @Override
    public Long getKey(int position) {
        if(entries.size() > position) {
            return entries.get(position).getId();
        }
        return -1L;
    }

    @Override
    public int getPosition(@NonNull Long key) {
        int position = 0;
        for(Entry e: entries) {
            if(e.getId() == key) {
                return position;
            }
            position++;
        }
        return position;
    }
}
