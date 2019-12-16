package com.example.accounts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import static androidx.core.content.ContextCompat.getDrawable;


public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private Context context;
    private List<Account> list;
    private List<String> selectedIds = new ArrayList<>();

    public MyAdapter(Context context, List<Account> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.list_account, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.title.setText(list.get(position).getName());
        String name = list.get(position).getName();

        if (selectedIds.contains(name)) {
            //if item is selected then,set foreground color of FrameLayout.
            holder.title.setBackground(getDrawable(context, R.drawable.rounded_list_element2));


        } else {
            //else remove selected item color.
            holder.title.setBackground(getDrawable(context, R.drawable.rounded_list_element));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public Account getItem(int position) {
        if (position == -1) return null;
        else
            return list.get(position);
    }

    public void setSelectedIds(List<String> selectedIds) {
        this.selectedIds = selectedIds;
        notifyDataSetChanged();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        FrameLayout rootView;

        MyViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.accountElement);
            rootView = itemView.findViewById(R.id.viewLayout);
        }
    }
}