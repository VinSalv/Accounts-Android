package com.example.accounts;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

public class TabAdapter extends FragmentStatePagerAdapter {
    private int mNumOfTabs;
    private ArrayList<AccountElement> listElem;

    TabAdapter(FragmentManager fm, int NumOfTabs, ArrayList<AccountElement> listElem) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.listElem = listElem;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return DynamicFragment.addFrag(listElem.get(position).getEmail(), listElem.get(position).getUser(), listElem.get(position).getPassword());
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}