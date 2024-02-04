package com.example.expensetracker.adapters;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensetracker.R;
import com.example.expensetracker.pojo.Entry;
import com.example.expensetracker.pojo.User;

import java.util.ArrayList;
import java.util.HashMap;

public class SharedUserAdapter extends RecyclerView.Adapter<SharedUserAdapter.ViewHolder> {
    public interface ValueUpdateListener {
        void valueUpdate(int newTotal);
    }
    static class SharedUser {
        String name;
        int value;
    }
    ArrayList<User> nameList;
    ArrayList<SharedUser> sharedUserList;
    ValueUpdateListener updateListener;
    int itemCount = 1;
    public SharedUserAdapter(ArrayList<User> names, ValueUpdateListener updateListener) {
        sharedUserList = new ArrayList<>();
        sharedUserList.add(null);
        sharedUserList.add(null);
        this.updateListener = updateListener;
    }

    private int getValueTotal() {
        int total = 0;
        for(SharedUser i : sharedUserList) {
            total += i.value;
        }
        return total;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_shared_user, parent, false);
        return new ViewHolder(view);
    }

    @NonNull
    public ArrayList<Entry.SharedUser> getSharedUserList(ArrayList<User> newUsers) {
        newUsers.clear();
        HashMap<String, User> map = new HashMap<>();
        for(User u : nameList) {
            map.put(u.getName(), u);
        }
        HashMap<String, Integer> omap = new HashMap<>();
        for(SharedUser su : sharedUserList) {
            if(omap.containsKey(su.name)) {
                Integer a = omap.get(su.name);
                omap.replace(su.name, (a != null ? a : 0) + su.value);
            } else {
                omap.put(su.name, su.value);
            }
        }
        ArrayList<Entry.SharedUser> finalList = new ArrayList<>();
        for(String n : omap.keySet()) {
            Integer a = omap.get(n);
            int sum = a == null ? 0 : a;
            if(map.containsKey(n)) {
                finalList.add(new Entry.SharedUser(map.get(n), sum));
            } else {
                User u = new User(0, n);
                newUsers.add(u);
                finalList.add(new Entry.SharedUser(u, sum));
            }
        }
        return finalList;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(position == 0) {
            holder.autoCompleteTextViewName.setText(R.string.expense_share_user_me);
            holder.autoCompleteTextViewName.setInputType(InputType.TYPE_NULL);
            holder.autoCompleteTextViewName.setFocusable(false);
            holder.autoCompleteTextViewName.setClickable(false);
        } else {
            ArrayList<String> names = new ArrayList<>();
            nameList.forEach(user -> names.add(user.getName()));
            holder.autoCompleteTextViewName.setAdapter(new ArrayAdapter<>(holder.autoCompleteTextViewName.getContext(), android.R.layout.simple_list_item_1, names));
            holder.autoCompleteTextViewName.setThreshold(1);
            holder.autoCompleteTextViewName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    sharedUserList.get(holder.getAdapterPosition()).name = holder.autoCompleteTextViewName.getText().toString();
                    if (s.length() > 0 && holder.getAdapterPosition() == itemCount) {
                        notifyItemInserted(itemCount + 1);
                        itemCount += 1;
                        sharedUserList.add(new SharedUser());
                    }
                }
            });
            holder.autoCompleteTextViewName.setOnFocusChangeListener((v, hasFocus) -> {
                if(!hasFocus) {
                    if(holder.autoCompleteTextViewName.getText().length() == 0 && holder.getAdapterPosition() < itemCount - 1) {
                        notifyItemRemoved(holder.getAdapterPosition());
                        itemCount -= 1;
                        sharedUserList.remove(holder.getAdapterPosition());
                    }
                }
            });
        }
        holder.editTextValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int num = 0;
                if(s.length() > 0) {
                    num = Integer.parseInt(s.toString());
                }
                sharedUserList.get(holder.getAdapterPosition()).value = num;
                updateListener.valueUpdate(getValueTotal());
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemCount + 1;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        AutoCompleteTextView autoCompleteTextViewName;
        EditText editTextValue;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            autoCompleteTextViewName = itemView.findViewById(R.id.shared_user_name);
            editTextValue = itemView.findViewById(R.id.shared_user_value);
        }
    }
}
