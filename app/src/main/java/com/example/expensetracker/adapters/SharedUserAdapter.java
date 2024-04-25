package com.example.expensetracker.adapters;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Pair;
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
        nameList = names;
        sharedUserList = new ArrayList<>();
        SharedUser meUser = new SharedUser();
        meUser.name = "Me";
        sharedUserList.add(meUser);
        sharedUserList.add(new SharedUser());
        this.updateListener = updateListener;
    }

    private int getValueTotal() {
        int total = 0;
        for(SharedUser i : sharedUserList) {
            if(i != null) {
                total += i.value;
            }
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
    public int getSharedUserList(ArrayList<Entry.SharedUser> sharedUsersList, ArrayList<Entry.SharedUser> newUsers) {
        newUsers.clear();
        HashMap<String, User> map = new HashMap<>();
        for(User u : nameList) {
            map.put(u.getName(), u);
        }
        HashMap<String, Integer> userMap = new HashMap<>();
        for(SharedUser su : sharedUserList) {
            if(su.name != null && !su.name.equals("Me")) {
                if (userMap.containsKey(su.name)) {
                    Integer a = userMap.get(su.name);
                    userMap.replace(su.name, (a != null ? a : 0) + su.value);
                } else {
                    userMap.put(su.name, su.value);
                }
            }
        }
        int miscIndex = -1;
        for(String n : userMap.keySet()) {
            Integer a = userMap.get(n);
            if(a != null && a != 0) {
                int sum = a;
                if (map.containsKey(n)) {
                    if(n.equals("Misc")) {
                        miscIndex = sharedUsersList.size();
                    }
                    sharedUsersList.add(new Entry.SharedUser(map.get(n), sum));
                } else {
                    newUsers.add(new Entry.SharedUser(new User(n), sum));
                }
            }
        }
        return miscIndex;
    }

    public int getMeTotal() {
        return sharedUserList.get(0).value;
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
