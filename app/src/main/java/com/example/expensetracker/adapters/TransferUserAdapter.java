package com.example.expensetracker.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class TransferUserAdapter extends BaseAdapter {

    ArrayList<String> meData;
    ArrayList<String> userData;
    boolean enableUsers;

    public TransferUserAdapter(ArrayList<String> me, ArrayList<String> user) {
        meData = new ArrayList<>(me);
        userData = new ArrayList<>(user);
        enableUsers = true;
    }

    @Override
    public int getCount() {
        if(enableUsers) {
            return userData.size() + meData.size();
        }
        return meData.size();
    }

    public void setEnableUsers(boolean enable) {
        enableUsers = enable;
    }

    String getData(int position) {
        if(position >= meData.size()) {
            return userData.get(position - meData.size());
        }
        return meData.get(position);
    }

    public boolean getEnableUsers() {
        return enableUsers;
    }

    @Override
    public Object getItem(int position) {
        return getData(position);
    }

    @Override
    public long getItemId(int position) {
        return position >= meData.size() ? 1 : 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(android.R.layout.simple_spinner_item, parent, false);
            TextView text = convertView.findViewById(android.R.id.text1);
            if(position == 0) {
                text.setText("Select an Option");
            } else {
                text.setText(getData(position - 1));
            }
        }
        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return super.getDropDownView(position + 1, convertView, parent);
    }
}
