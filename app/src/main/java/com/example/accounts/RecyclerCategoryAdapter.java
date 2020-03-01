package com.example.accounts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;

import static androidx.core.content.ContextCompat.getDrawable;

public class RecyclerCategoryAdapter extends RecyclerView.Adapter<RecyclerCategoryAdapter.MyViewHolder> implements ItemMoveCategoryCallback.ItemTouchHelperContract {

    private ArrayList<String> selectedIds = new ArrayList<>();
    private ArrayList<Category> data;
    private Context context;
    private ManageUser mngUsr = new ManageUser();
    private ManageCategory mngCat = new ManageCategory();
    private User usr;

    public RecyclerCategoryAdapter(Context context, ArrayList<Category> data, User usr) {
        this.context = context;
        this.data = data;
        this.usr = usr;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.list_category, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.mTitle.setText(data.get(position).getCat());
        String name = data.get(position).getCat();
        if (selectedIds.contains(name)) {
            holder.mTitle.setBackground(getDrawable(context, R.drawable.rounded_list_element2));
        } else {
            holder.mTitle.setBackground(getDrawable(context, R.drawable.rounded_list_element));
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    Category getItem(int position) {
        if (position == -1) return null;
        else
            return data.get(position);
    }

    void setSelectedIds(ArrayList<String> selectedIds) {
        this.selectedIds = selectedIds;
        notifyDataSetChanged();
    }

    @Override
    public void onRowMoved(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(data, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(data, i, i - 1);
            }
        }
        mngCat.serializationListCategory(context, data, usr.getUser());
        ArrayList<User> listUsr = mngUsr.deserializationListUser(context);
        listUsr.remove(usr);
        usr.setSort(3);
        listUsr.add(usr);
        mngUsr.serializationListUser(context, listUsr);
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onRowSelected(MyViewHolder myViewHolder) {
        myViewHolder.mTitle.setBackground(getDrawable(context, R.drawable.rounded_list_element2));
    }

    @Override
    public void onRowClear(MyViewHolder myViewHolder) {
        myViewHolder.mTitle.setBackground(getDrawable(context, R.drawable.rounded_list_element));
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        FrameLayout rootView;
        private TextView mTitle;

        public MyViewHolder(View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.categoryElement);
            rootView = itemView.findViewById(R.id.catActivityLay);
        }
    }
}