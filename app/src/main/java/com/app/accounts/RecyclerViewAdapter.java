package com.app.accounts;

import static androidx.core.content.ContextCompat.getDrawable;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> implements ItemMoveCallback.ItemTouchHelperContract {
    private ManageUser mngUsr = new ManageUser();
    private ManageCategory mngCat = new ManageCategory();
    private ArrayList<String> selectedIds = new ArrayList<>();
    private ArrayList<Account> listAccount;
    private User usr;
    private Category category;
    private Context context;

    RecyclerViewAdapter(Context context, User usr, Category category) {
        this.context = context;
        this.listAccount = category.getListAcc();
        this.usr = usr;
        this.category = category;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.list_account, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.mTitle.setText(listAccount.get(position).getName());
        String name = listAccount.get(position).getName();
        if (selectedIds.contains(name)) {
            holder.mTitle.setBackground(getDrawable(context, R.drawable.rounded_list_element2));
        } else {
            holder.mTitle.setBackground(getDrawable(context, R.drawable.rounded_list_element));
        }
    }

    @Override
    public int getItemCount() {
        return listAccount.size();
    }

    Account getItem(int position) {
        if (position == -1) return null;
        else
            return listAccount.get(position);
    }

    void setSelectedIds(ArrayList<String> selectedIds) {
        this.selectedIds = selectedIds;
        notifyDataSetChanged();
    }

    @Override
    public void onRowMoved(int fromPosition, int toPosition) {
        ArrayList<Category> listCategory = mngCat.deserializationListCategory(RecyclerViewAdapter.this.context, usr.getUser());
        mngCat.removeFileCategory(context, usr.getUser());

        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(listAccount, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(listAccount, i, i - 1);
            }
        }

        ArrayList<Category> listCategory2 = new ArrayList<>();
        for (Category singleCategory : listCategory) {
            if (!singleCategory.getCat().toLowerCase().equals(category.getCat().toLowerCase())) {
                listCategory2.add(singleCategory);
            } else {
                listCategory2.add(new Category(category.getCat(), listAccount, 3));
            }
        }
        mngCat.serializationListCategory(RecyclerViewAdapter.this.context, listCategory2, usr.getUser());

        ArrayList<User> listUsr = mngUsr.deserializationListUser(RecyclerViewAdapter.this.context);
        listUsr.remove(usr);
        usr.setSort(3);
        listUsr.add(usr);
        mngUsr.serializationListUser(RecyclerViewAdapter.this.context, listUsr);

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

    static class MyViewHolder extends RecyclerView.ViewHolder {
        FrameLayout rootView;
        private TextView mTitle;

        MyViewHolder(View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.accountElement);
            rootView = itemView.findViewById(R.id.viewActivityLay);
        }
    }
}