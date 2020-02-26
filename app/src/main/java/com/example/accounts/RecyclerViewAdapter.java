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
import java.util.Objects;

import static androidx.core.content.ContextCompat.getDrawable;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> implements ItemMoveCallback.ItemTouchHelperContract {

    private ArrayList<Account> data;
    private Context context;
    private ManageUser mngUsr = new ManageUser();
    private ManageAccount mngAcc = new ManageAccount();
    private User usr;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView mTitle;
        FrameLayout rootView;

        public MyViewHolder(View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.accountElement);
            rootView = itemView.findViewById(R.id.viewActivityLay);
        }
    }

    public RecyclerViewAdapter(Context context, ArrayList<Account> data, User usr) {
        this.context = context;
        this.data = data;
        this.usr = usr;
        usr = mngUsr.findUser(mngUsr.deserializationListUser(context), Objects.requireNonNull(usr).getUser());
        if (usr == null) return;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.list_account, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.mTitle.setText(data.get(position).getName());
    }


    @Override
    public int getItemCount() {
        return data.size();
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
        mngAcc.serializationListAccount(context, data, usr.getUser());
        ArrayList<User> listUsr =mngUsr.deserializationListUser(context);
        listUsr.remove(usr);
        usr.setSort(3);
        listUsr.add(usr);
        mngUsr.serializationListUser(context,listUsr);
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
}