package com.example.accounts;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

class ListViewAdapter extends ArrayAdapter<String> {
    private Context context;
    private LayoutInflater inflater;
    private List<String> accounts;
    private SparseBooleanArray mSelectedItemsIds;

    public ListViewAdapter(Context context, int resourceId, List<String> accounts) {
        super(context, resourceId, accounts);
        mSelectedItemsIds = new SparseBooleanArray();
        this.context = context;
        this.accounts = accounts;
        inflater = LayoutInflater.from(context);
    }

    private class ViewHolder {
        TextView account;
    }

    public View getView(int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.list_account, null);
            holder.account = (TextView) view.findViewById(R.id.accountButton);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // Capture position and set to the TextViews
        holder.account.setText(accounts.get(position));
        return view;
    }

    @Override
    public void remove(String object) {
        accounts.remove(object);
        notifyDataSetChanged();
    }

    public List<String> getWorldPopulation() {
        return accounts;
    }

    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }

    public void removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    public void selectView(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position, value);
        else
            mSelectedItemsIds.delete(position);
        notifyDataSetChanged();
    }

    public int getSelectedCount() {
        return mSelectedItemsIds.size();
    }

    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }
}
