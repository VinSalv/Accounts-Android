package com.example.accounts;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerCategoryChoseAdapter extends RecyclerView.Adapter<RecyclerCategoryChoseAdapter.MyViewHolder> {
    public boolean clicked = false;
    private ArrayList<Category> listCategory;
    private Context context;

    RecyclerCategoryChoseAdapter(Context context, ArrayList<Category> listCategory) {
        this.context = context;
        this.listCategory = listCategory;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.list_category, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.mTitle.setText(listCategory.get(position).getCat());
        if (clicked) {
            holder.mTitle.setVisibility(View.GONE);
        } else {
            holder.mTitle.setVisibility(View.VISIBLE);
        }
        holder.mTitle.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                clicked = true;
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listCategory.size();
    }

    Category getItem(int position) {
        if (position == -1) return null;
        else
            return listCategory.get(position);
    }

    public void setClicked(boolean clicked) {
        this.clicked = clicked;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        FrameLayout rootView;
        private TextView mTitle;

        MyViewHolder(View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.categoryElement);
            rootView = itemView.findViewById(R.id.catActivityLay);
        }
    }
}