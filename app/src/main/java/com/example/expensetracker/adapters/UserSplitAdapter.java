package com.example.expensetracker.adapters;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensetracker.R;
import com.example.expensetracker.database.DBManager;
import com.example.expensetracker.pojo.User;

import java.util.ArrayList;
import java.util.Locale;

public class UserSplitAdapter extends RecyclerView.Adapter<UserSplitAdapter.ViewHolder> {
    private static class UserValues {
        User user;
        int value;
        public UserValues(User user, int value) {
            this.user = user;
            this.value = value;
        }
    }
    public interface OnClickListener {
        void onClick(User u);
    }
    private OnClickListener clickListener;
    private ArrayList<UserValues> users;
    public UserSplitAdapter(ArrayList<User> userList, OnClickListener onClickListener) {
        users = new ArrayList<>();
        clickListener = onClickListener;
        for(User user : userList) {
            users.add(new UserValues(user, DBManager.getDBManagerInstance().getUserShare(user.getId())));
        }
    }

    @NonNull
    @Override
    public UserSplitAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_user_split, parent, false);
        return new UserSplitAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.name.setText(users.get(position).user.getName());
        int value = users.get(position).value;
        if(value > 0) {
            holder.value.setTextColor(holder.itemView.getResources().getColor(R.color.profitGreen, holder.itemView.getContext().getTheme()));
        } else if(value < 0) {
            holder.value.setTextColor(holder.itemView.getResources().getColor(R.color.lossRed, holder.itemView.getContext().getTheme()));
        } else {
            holder.value.setTextColor(holder.itemView.getResources().getColor(android.R.color.white, holder.itemView.getContext().getTheme()));
        }
        holder.value.setText(String.valueOf(value));
        holder.itemView.setOnClickListener(v -> {
            clickListener.onClick(users.get(position).user);
        });
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
