package com.example.accounts;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private static final String TAG = "MyAdapter";
    private Context context;
    private List<Account> mDataset;


    public MyAdapter(Context context, List<Account> mDataset) {
        this.context = context;
        this.mDataset = mDataset;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView account;
        public RelativeLayout relLayList;

        public MyViewHolder(View v) {
            super(v);
            account = v.findViewById(R.id.accountElement);
            relLayList = v.findViewById(R.id.relLayList);
        }
    }

    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_account, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");
        holder.account.setText(mDataset.get(position).getName());
        holder.relLayList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked on: " + mDataset.get(position).getName());
                Toast.makeText(context, mDataset.get(position).getName(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}
