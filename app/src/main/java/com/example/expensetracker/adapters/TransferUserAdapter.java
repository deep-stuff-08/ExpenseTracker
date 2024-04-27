package com.example.expensetracker.adapters;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.expensetracker.R;

import java.util.ArrayList;

public class TransferUserAdapter extends BaseAdapter {

    public static class State {
        boolean isUserEnabled;
        int selectedItemPositionInOtherSpinner;
        String selectedItemInOtherSpinner;

        State() {
            reset();
        }

        public void reset() {
            isUserEnabled = true;
            selectedItemInOtherSpinner = "";
            selectedItemPositionInOtherSpinner = -1;
        }
    }
    ArrayList<String> meData;
    ArrayList<String> userData;
    State s;
    public TransferUserAdapter(ArrayList<String> me, ArrayList<String> user) {
        meData = new ArrayList<>(me);
        userData = new ArrayList<>(user);
        s = new State();
    }

    public State getState() {
        State new_s = new State();
        new_s.isUserEnabled = s.isUserEnabled;
        new_s.selectedItemInOtherSpinner = s.selectedItemInOtherSpinner;
        new_s.selectedItemPositionInOtherSpinner = s.selectedItemPositionInOtherSpinner;
        return new_s;
    }

    public void setState(State new_s) {
        s.isUserEnabled = new_s.isUserEnabled;
        s.selectedItemInOtherSpinner = new_s.selectedItemInOtherSpinner;
        s.selectedItemPositionInOtherSpinner = new_s.selectedItemPositionInOtherSpinner;
    }
    public void resetState() {
        s.reset();
    }

    @Override
    public int getCount() {
        int dataCount = meData.size() + 1;
        if(s.isUserEnabled) {
            dataCount += userData.size();
        }
        if(!s.selectedItemInOtherSpinner.isEmpty()) {
            dataCount -= 1;
        }
        if(s.selectedItemPositionInOtherSpinner >= meData.size()) {
            dataCount += 1;
        }
        return dataCount;
    }

    public int getItemPosition(String item) {
        if(userData.contains(item)) {
            return meData.size() + userData.indexOf(item) + 1;
        } else if(meData.contains(item)) {
            return meData.indexOf(item) + 1;
        } else {
            return 0;
        }
    }

    public void setSelectedItem(String item) {
        s.selectedItemInOtherSpinner = item;
        s.isUserEnabled = !userData.contains(item);
        if(s.isUserEnabled) {
            s.selectedItemPositionInOtherSpinner = meData.indexOf(item);
        } else {
            s.selectedItemPositionInOtherSpinner = meData.size() + userData.indexOf(item);
        }
    }

    String getData(int position) {
        if(s.selectedItemPositionInOtherSpinner != -1 && position > s.selectedItemPositionInOtherSpinner) {
            position += 1;
        }
        if(position == 0) {
            return "Select an Option";
        }
        position -= 1;
        if(position >= meData.size()) {
            return userData.get(position - meData.size());
        }
        return meData.get(position);
    }

    public String getDataUnfiltered(int position) {
        if(position == 0) {
            return "Select an Option";
        }
        position -= 1;
        if(position >= meData.size()) {
            return userData.get(position - meData.size());
        }
        return meData.get(position);
    }

    @Override
    public Object getItem(int position) {
        return getData(position);
    }

    @Override
    public long getItemId(int position) {
        position -= 1;
        return position >= meData.size() ? 1 : 0;
    }

    @Override
    public boolean isEnabled(int position) {
        if(position == 0) {
            return false;
        }
        return super.isEnabled(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.adapter_combobox, parent, false);
            TextView text = convertView.findViewById(R.id.spinner_category_text);
            text.setText(getData(position));
            if(position == 0) {
                text.setEnabled(false);
            }
        }
        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return super.getDropDownView(position, convertView, parent);
    }
}
