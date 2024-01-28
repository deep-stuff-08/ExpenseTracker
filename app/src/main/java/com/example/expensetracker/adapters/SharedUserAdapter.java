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

public class SharedUserAdapter extends RecyclerView.Adapter<SharedUserAdapter.ViewHolder> {
    public interface ValueUpdateListener {
        void valueUpdate(int newTotal);
    }
    ArrayList<String> nameList;
    ArrayList<Entry.SharedUser> sharedUserList;
    ValueUpdateListener updateListener;
    int itemCount = 1;
    public SharedUserAdapter(ArrayList<User> names, ValueUpdateListener updateListener) {
        nameList = new ArrayList<>();
        names.forEach(user -> nameList.add(user.getName()));
        sharedUserList = new ArrayList<>();
        sharedUserList.add(new Entry.SharedUser("Me", 0));
        sharedUserList.add(new Entry.SharedUser());
        this.updateListener = updateListener;
    }

    private int getValueTotal() {
        int total = 0;
        for(Entry.SharedUser i : sharedUserList) {
            total += i.getValue();
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
    public ArrayList<Entry.SharedUser> getSharedUserList() {
        return sharedUserList;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(position == 0) {
            holder.autoCompleteTextViewName.setText(R.string.expense_share_user_me);
            holder.autoCompleteTextViewName.setInputType(InputType.TYPE_NULL);
            holder.autoCompleteTextViewName.setFocusable(false);
            holder.autoCompleteTextViewName.setClickable(false);
        } else {
            holder.autoCompleteTextViewName.setAdapter(new ArrayAdapter<>(holder.autoCompleteTextViewName.getContext(), android.R.layout.simple_list_item_1, nameList));
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
                    if (s.length() > 0 && holder.getAdapterPosition() == itemCount - 1) {
                        notifyItemInserted(itemCount);
                        itemCount += 1;
                        sharedUserList.get(holder.getAdapterPosition()).setName(holder.autoCompleteTextViewName.getText().toString());
                        sharedUserList.add(new Entry.SharedUser());
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
                sharedUserList.get(holder.getAdapterPosition()).setValue(Integer.valueOf(s.toString()));
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
