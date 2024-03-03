package com.example.expensetracker.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensetracker.R;
import com.example.expensetracker.pojo.User;

import java.util.ArrayList;

public class UserSplitAdapter extends RecyclerView.Adapter<UserSplitAdapter.ViewHolder> {
    private ArrayList<User> users;
    public UserSplitAdapter(ArrayList<User> userList) {
        users = userList;
    }

    @NonNull
    @Override
    public UserSplitAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_user_split, parent, false);
        return new UserSplitAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.name.setText(users.get(position).getName());
        holder.value.setText("0");
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView value;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.user_name);
            value = itemView.findViewById(R.id.user_value);
        }
    }
}
